package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chibatching.kotpref.blockingBulk
import com.chibatching.kotpref.bulk
import com.google.android.gms.location.LocationSettingsStates
import com.google.gson.Gson
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.powerManager
import org.jetbrains.anko.startService
import org.jetbrains.anko.stopService
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.extensions.getActivities
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.extensions.startForegroundService
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.models.BasePoint
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.models.TelemetryState
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt

/**
 * МП должно исключать генерацию более одного события в одну секунду.
 * Данные события должны генерироваться по факту прохождения дистанции в 200 метров, повороте, остановке или начале движения,
 * а также по времени не реже чем:
 * · Для состояния стоянка - 5 минут
 * · Для состояния движения и остановка - 1 минута
 */
// https://www.rabbitmq.com/api-guide.html
class TelemetryService : BaseService(), TelemetryListener {

    val db: Database by instance()

    val preferences: Preferences by instance()

    val gson: Gson by instance()

    private lateinit var locationManager: LocationManager

    private lateinit var broadcastManager: LocalBroadcastManager

    private var wakeLock: PowerManager.WakeLock? = null

    private var timer: ScheduledFuture<*>? = null

    private val factory = ConnectionFactory()

    private var connection: Connection? = null

    private var channel: Channel? = null

    @Volatile
    private var isRunning = false

    private var basePoint: BasePoint? = null

    private var lastEventTime: DateTime? = null

    private var locationCounter = AtomicLong(-1L)

    private val lock = Any()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        startForeground(
            Int.MAX_VALUE, NotificationCompat.Builder(applicationContext, CHANNEL_DEFAULT)
                .setSmallIcon(R.drawable.ic_gps_fixed_white_24dp)
                .setContentTitle("Фоновой сервис")
                .setOngoing(true)
                .build()
        )
        acquireWakeLock()
        broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        locationManager = LocationManager(applicationContext, this)
        startTelemetry()
        factory.apply {
            val address = preferences.mainTelemetryAddress.split(":")
            host = address[0]
            port = try {
                address.getOrNull(1)?.toInt() ?: 5672
            } catch (e: Throwable) {
                Timber.e(e)
                5672
            }
        }
        val executor = Executors.newScheduledThreadPool(1)
        timer = executor.scheduleAtFixedRate({
            // background thread here
            if (activityManager.getActivities(packageName) <= 0 || !preferences.isLoggedIn) {
                abortConnection()
                stopForeground(true)
                stopSelf()
                return@scheduleAtFixedRate
            }
            val locationDelay = locationCounter.incrementAndGet() * TIMER_INTERVAL
            when {
                locationDelay < LOCATION_MIN_DELAY -> {
                    onLocationAvailability(false)
                }
                locationDelay < LOCATION_MIN_DELAY -> {
                    synchronized(lock) {
                        lastEventTime = null
                        basePoint = null
                    }
                    addPointEvent {
                        lastEventTime?.let { lastTime ->
                            val point = basePoint
                            if (point != null) {
                                var delay = 0L
                                when (point.state) {
                                    TelemetryState.MOVING, TelemetryState.STOPPING -> {
                                        val now = DateTime.now()
                                        if (now.millis - lastTime.withZone(now.zone).millis >= MOVING_DELAY) {
                                            delay = MOVING_DELAY
                                        }
                                    }
                                    TelemetryState.PARKING -> {
                                        val now = DateTime.now()
                                        if (now.millis - lastTime.withZone(now.zone).millis >= PARKING_DELAY) {
                                            delay = PARKING_DELAY
                                        }
                                    }
                                    else -> {
                                    }
                                }
                                if (delay > 0L) {
                                    Timber.i("Inserting event after delay $delay")
                                    preferences.blockingBulk {

                                        LocationEvent(point, tokenId, packageId, mileage.roundToInt()).also {
                                            // debug info
                                            it.state = point.state.name
                                            it.waiting = true
                                            lastEventTime = it.data.whenTime
                                        }
                                        packageId++
                                    }
                                }
                            }
                        }
                        return@addPointEvent null
                    }
                }
            }
            db.locationDao().getLastSendEvent()?.let {
                if (!it.location.isValid) {
                    db.locationDao().delete(it.location)
                    checkCount()
                    return@scheduleAtFixedRate
                }
                val user = it.token.carId.toString()
                val pswd = it.token.token
                it.location.authKey = it.token.token
                try {
                    factory.apply {
                        if (connection?.isOpen == false || channel?.isOpen == false || username != user || password != pswd) {
                            abortConnection()
                            username = user
                            password = pswd
                            connection = newConnection().apply {
                                channel = createChannel()
                            }
                        }
                    }
                    val json = gson.toJson(it.location)
                    Timber.d("LocationEvent: $json")
                    channel?.apply {
                        txSelect()
                        channel?.basicPublish(
                            "cars",
                            it.token.carId.toString(),
                            null,
                            json.toByteArray(Charsets.UTF_8)
                        )
                        txCommit()
                    }
                    db.locationDao().markAsSent(it.location.id ?: 0L)
                    checkCount()
                } catch (e: Throwable) {
                    Timber.e(e)
                    abortConnection()
                }
            }
        }, 0L, TIMER_INTERVAL, TimeUnit.MILLISECONDS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.hasExtra(EXTRA_TELEMETRY_TASK)) {
                if (it.getBooleanExtra(EXTRA_TELEMETRY_TASK, false)) {
                    startTelemetry()
                } else {
                    stopTelemetry()
                }
            }
        }
        return START_STICKY
    }

    private fun checkCount() {
        val locationCount = db.locationDao().getSendCount()
        if (locationCount <= 0) {
            broadcastManager.sendBroadcast(Intent(ACTION_CLOUD))
        }
    }

    override fun startTelemetry() {
        isRunning = true
        locationManager.requestUpdates()
    }

    override fun stopTelemetry() {
        isRunning = false
        locationManager.removeUpdates()
        synchronized(lock) {
            lastEventTime = null
            basePoint = null
        }
    }

    @SuppressLint("WakelockTimeout")
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name).apply {
                acquire()
            }
        }
    }

    override fun onLocationStart(enabled: Boolean, ttffMillis: Int) {
        if (!enabled || ttffMillis > 0) {
            locationCounter.set(0L)
        }
        onLocationAvailability(true)
    }

    override fun onLocationStop(disabled: Boolean) {
        onLocationAvailability(false)
    }

    /**
     * Will not be called here
     */
    override fun onLocationState(state: LocationSettingsStates?) {}

    override fun onLocationAvailability(available: Boolean) {
        broadcastManager.sendBroadcast(Intent(ACTION_LOCATION).apply {
            putExtra(EXTRA_SYNC_AVAILABILITY, available)
        })
    }

    override fun onLocationChanged(location: Location, satellitesCount: Int) {
        val newLocation = SimpleLocation(location).apply {
            satellites = satellitesCount
        }
        onLocationResult(newLocation)
        locationCounter.set(0L)
        //launch {
        //   withContext(Dispatchers.IO) {
        /*if (activityManager.getActivities(packageName) <= 0 || !preferences.isLoggedIn) {
            abortConnection()
            stopForeground(true)
            stopSelf()
            //return@withContext
        }*/
        addPointEvent { point ->
            if (point != null) {
                preferences.blockingBulk {
                    val space = point.updateLocation(newLocation)
                    val distance: Float
                    if (point.state != TelemetryState.PARKING) {
                        distance = mileage + space
                        mileage = distance
                    } else {
                        distance = mileage
                    }
                    point.replaceWith()?.let { state ->
                        Timber.i("Replace state with $state")
                        basePoint = BasePoint(newLocation, state)
                        return@addPointEvent LocationEvent(point, tokenId, packageId, distance.roundToInt()).also {
                            // debug info
                            it.state = state.name
                            lastEventTime = it.data.whenTime
                            packageId++
                        }
                    }
                }
            } else {
                basePoint = BasePoint(newLocation)
            }
            return@addPointEvent null
        }
        //   }
        //}
    }

    override fun onLocationResult(location: SimpleLocation) {
        preferences.bulk {
            latitude = location.latitude.toFloat()
            longitude = location.longitude.toFloat()
            locationTime = location.locationTime.toString(PATTERN_DATETIME)
        }
        broadcastManager.sendBroadcast(Intent(ACTION_LOCATION).apply {
            putExtra(EXTRA_SYNC_LOCATION, location)
        })
    }

    /**
     * Cannot be called on UI thread because of [android.os.NetworkOnMainThreadException]
     */
    private fun abortConnection() {
        try {
            channel?.abort()
        } catch (e: Throwable) {
            Timber.e(e)
        }
        try {
            connection?.abort()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            it.release()
            wakeLock = null
        }
    }

    override fun onDestroy() {
        stopTelemetry()
        timer?.cancel(true)
        releaseWakeLock()
        super.onDestroy()
    }

    private inline fun addPointEvent(block: (BasePoint?) -> LocationEvent?) {
        var event: LocationEvent? = null
        synchronized(lock) {
            if (isRunning) {
                event = block(basePoint)
            }
        }
        event?.let {
            db.locationDao().insert(it)
        }
    }

    companion object {

        private const val TIMER_INTERVAL = 1500L

        // Для состояния стоянка - 5 минут
        private const val PARKING_DELAY = 5 * 60_000L

        // Для состояния движения и остановка - 1 минута
        private const val MOVING_DELAY = 60_000L

        /**
         * Min timeout of no input locations
         */
        private const val LOCATION_MIN_DELAY = 15_000L

        /**
         * Max timeout of no input locations
         */
        private const val LOCATION_MAX_DELAY = 60_000L

        /**
         * @return true if service is running
         */
        @Throws(SecurityException::class)
        fun start(context: Context, vararg params: Pair<String, Any?>): Boolean = context.run {
            if (!areGranted(*DANGER_PERMISSIONS)) {
                return false
            }
            return if (!activityManager.isRunning<TelemetryService>()) {
                startForegroundService<TelemetryService>() != null
            } else {
                startService<TelemetryService>(*params) != null
            }
        }

        /**
         * Currently this shouldn't be called outside
         * @return true if service is stopped
         */
        @Suppress("unused")
        private fun stop(context: Context): Boolean = context.run {
            if (activityManager.isRunning<TelemetryService>()) {
                return stopService<TelemetryService>()
            }
            return true
        }
    }
}