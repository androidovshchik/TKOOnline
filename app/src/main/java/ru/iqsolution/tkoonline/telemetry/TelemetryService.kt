@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.telemetry

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import com.chibatching.kotpref.bulk
import com.google.android.gms.location.LocationSettingsStates
import com.google.gson.Gson
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.joda.time.DateTime
import org.kodein.di.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.*
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.models.BasePoint
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.models.TelemetryConfig
import ru.iqsolution.tkoonline.models.TelemetryState
import ru.iqsolution.tkoonline.screens.base.user.UserActivity
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * МП должно исключать генерацию более одного события в одну секунду.
 * Данные события должны генерироваться по факту прохождения дистанции в 200 метров, повороте, остановке или начале движения,
 * а также по времени не реже чем:
 * · Для состояния стоянка - 5 минут
 * · Для состояния движения и остановка - 1 минута
 *
 * Telemetry URI: amqp://$carId:$accessToken@$mainTelemetryAddress
 */
// https://www.rabbitmq.com/api-guide.html
class TelemetryService : BaseService(), TelemetryListener {

    private val locationManager: LocationManager by instance()

    private val db: Database by instance()

    private val fileManager: FileManager by instance()

    private val preferences: Preferences by instance()

    private val gsonPretty: Gson by instance(arg = true)

    private val gsonMin: Gson by instance(arg = false)

    private lateinit var config: TelemetryConfig

    private var timer: ScheduledFuture<*>? = null

    private val factory = ConnectionFactory()

    private var connection: Connection? = null

    private var channel: Channel? = null

    private val preferenceHolder = PreferenceHolder()

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
    @Suppress("ConstantConditionIf", "SpellCheckingInspection")
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
        config = try {
            if (BuildConfig.PROD) {
                TelemetryConfig()
            } else {
                fileManager.run {
                    if (configFile.exists()) {
                        gsonMin.fromJson(configFile.readText(), TelemetryConfig::class.java)
                    } else {
                        launch {
                            withContext(Dispatchers.IO) {
                                writeFile(configFile) {
                                    it.write(
                                        gsonPretty.toJson(
                                            Class.forName("$packageName.models.TelemetryDesc").newInstance()
                                        ).toByteArray()
                                    )
                                }
                            }
                        }
                        TelemetryConfig()
                    }
                }
            }
        } catch (e: Throwable) {
            Timber.e(e)
            TelemetryConfig()
        }
        preferenceHolder.init(preferences)
        startTelemetry()
        factory.apply {
            val address = preferences.mainTelemetryAddress.split(":")
            host = address[0]
            port = try {
                address.getOrNull(1)?.toInt() ?: DEFAULT_PORT
            } catch (e: Throwable) {
                Timber.e(e)
                DEFAULT_PORT
            }
        }
        val executor = Executors.newSingleThreadScheduledExecutor()
        timer = executor.scheduleAtFixedRate({
            // background thread here
            if (!checkActivity()) {
                return@scheduleAtFixedRate
            }
            val counter = locationCounter.incrementAndGet()
            val locationDelay = counter * config.timerInterval
            if (locationDelay > config.locationMinDelay) {
                onLocationAvailability(false)
            }
            addEventSync {
                if (locationDelay > config.locationMaxDelay) {
                    lastEventTime = null
                    basePoint = null
                    if (!BuildConfig.PROD) {
                        if (counter % 2 == 0L) {
                            bgToast("Не удается определить местоположение")
                        }
                    }
                    return@addEventSync null
                }
                val lastTime = lastEventTime ?: return@addEventSync null
                if (it != null) {
                    var eventDelay = 0L
                    when (it.state) {
                        TelemetryState.MOVING, TelemetryState.STOPPING -> {
                            if (lastTime.isEarlier(config.movingDelay, TimeUnit.MILLISECONDS)) {
                                eventDelay = config.movingDelay
                            }
                        }
                        TelemetryState.PARKING -> {
                            if (lastTime.isEarlier(config.parkingDelay, TimeUnit.MILLISECONDS)) {
                                eventDelay = config.parkingDelay
                            }
                        }
                        else -> {
                        }
                    }
                    if (eventDelay > 0L) {
                        Timber.i("Event after delay $eventDelay")
                        preferenceHolder.run {
                            return@addEventSync LocationEvent(it, tokenId, packageId, mileage, true).also { event ->
                                packageId++
                                lastEventTime = event.data.whenTime
                            }
                        }
                    }
                }
                null
            }
            if (!connectivityManager.isConnected) {
                return@scheduleAtFixedRate
            }
            db.locationDao().getLastSendEvent()?.let {
                if (!it.location.isValid) {
                    db.locationDao().delete(it.location)
                    checkCount()
                    return@scheduleAtFixedRate
                }
                val user = it.token.carId.toString()
                val pswd = it.token.token
                try {
                    factory.apply {
                        if (connection?.isOpen != true || channel?.isOpen != true || username != user || password != pswd) {
                            abortConnection()
                            username = user
                            password = pswd
                            connection = newConnection().apply {
                                channel = createChannel()
                            }
                        }
                    }
                    val json = gsonMin.toJson(it)
                    Timber.d("LocationEvent: $json")
                    channel!!.apply {
                        txSelect()
                        basicPublish("cars", user, null, json.toByteArray(Charsets.UTF_8))
                        txCommit()
                    }
                    db.locationDao().markAsSent(it.location.id ?: 0L)
                    checkCount()
                } catch (e: Throwable) {
                    Timber.e(e)
                    abortConnection()
                }
            }
        }, 0L, config.timerInterval, TimeUnit.MILLISECONDS)
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

    override fun startTelemetry() {
        isRunning = true
        locationManager.requestUpdates(config.locationInterval)
    }

    override fun stopTelemetry() {
        isRunning = false
        locationManager.removeUpdates()
        synchronized(lock) {
            lastEventTime = null
            basePoint = null
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
        preferenceHolder.save(preferences)
        sendBroadcast(Intent(ACTION_LOCATION).apply {
            putExtra(EXTRA_SYNC_AVAILABILITY, available)
        })
    }

    override fun onLocationChanged(location: Location, satellitesCount: Int) {
        val newLocation = SimpleLocation(location).apply {
            satellites = satellitesCount
        }
        onLocationAvailability(true)
        onLocationResult(newLocation)
        locationCounter.set(0L)
        launch {
            withContext(Dispatchers.IO) {
                if (!checkActivity()) {
                    return@withContext
                }
                addEventSync {
                    if (it != null) {
                        preferenceHolder.apply {
                            val space = it.updateLocation(newLocation)
                            val distance: Float
                            if (it.state != TelemetryState.PARKING) {
                                distance = mileage + space
                                mileage = distance
                            } else {
                                distance = mileage
                            }
                            it.replaceWith(config)?.let { state ->
                                Timber.i("Replace state with $state")
                                basePoint = BasePoint(newLocation, state)
                                return@addEventSync LocationEvent(it, tokenId, packageId, distance).also { event ->
                                    packageId++
                                    lastEventTime = event.data.whenTime
                                }
                            }
                        }
                    } else {
                        Timber.i("Init base point")
                        basePoint = BasePoint(newLocation)
                    }
                    null
                }
            }
        }
    }

    /**
     * Called also from [onLocationChanged]
     */
    override fun onLocationResult(location: SimpleLocation) {
        preferences.bulk {
            latitude = location.latitude.toFloat()
            longitude = location.longitude.toFloat()
            locationTime = location.locationTime.toString(PATTERN_DATETIME_ZONE)
        }
        sendBroadcast(Intent(ACTION_LOCATION).apply {
            putExtra(EXTRA_SYNC_LOCATION, location)
        })
    }

    @WorkerThread
    private fun checkCount() {
        sendBroadcast(Intent(ACTION_ROUTE))
        val locationCount = db.locationDao().getSendCount()
        if (locationCount <= 0) {
            sendBroadcast(Intent(ACTION_CLOUD))
        }
    }

    @WorkerThread
    private fun checkActivity(): Boolean {
        val name = activityManager.getTopActivity(packageName)
        if (name.isNullOrBlank() || !UserActivity::class.java.isAssignableFrom(Class.forName(name))) {
            // NOTICE without saving in persistent storage
            preferenceHolder.logout()
            abortConnection()
            stopForeground(true)
            stopSelf()
            return false
        }
        return true
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

    override fun onDestroy() {
        preferenceHolder.save(preferences)
        stopTelemetry()
        timer?.cancel(true)
        releaseWakeLock()
        super.onDestroy()
    }

    @WorkerThread
    private inline fun addEventSync(block: (BasePoint?) -> LocationEvent?) {
        var event: LocationEvent? = null
        synchronized(lock) {
            if (isRunning) {
                event = block(basePoint)
            }
        }
        event?.let {
            db.locationDao().insert(it)
            sendBroadcast(Intent(ACTION_ROUTE))
        }
    }

    companion object {

        private const val DEFAULT_PORT = 5672

        /**
         * @return true if service is running
         */
        @Throws(SecurityException::class)
        fun start(context: Context, vararg params: Pair<String, Any?>): Boolean = context.run {
            if (!areGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
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