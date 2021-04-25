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
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.startService
import org.jetbrains.anko.stopService
import org.joda.time.DateTime
import org.kodein.di.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.*
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.models.BasePoint
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.models.TelemetryConfig
import ru.iqsolution.tkoonline.models.TelemetryState
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import timber.log.Timber
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

    private val preferences: Preferences by instance()

    private val gson: Gson by instance(arg = false)

    private lateinit var config: TelemetryConfig

    private val factory = ConnectionFactory()

    private var connection: Connection? = null

    private var channel: Channel? = null

    private val preferenceHolder = PreferenceHolder()

    private var basePoint: BasePoint? = null

    private var lastEventTime: DateTime? = null

    private var locationCounter = AtomicLong(-1L)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    @Suppress("ConstantConditionIf", "SpellCheckingInspection")
    override fun onCreate() {
        super.onCreate()
        startForeground(
            Int.MAX_VALUE, NotificationCompat.Builder(applicationContext, CHANNEL_DEFAULT)
                .setSmallIcon(R.drawable.ic_bus)
                .setContentTitle("Фоновой сервис")
                .setOngoing(true)
                .build()
        )
        acquireWakeLock()
        config = TelemetryConfig()
        preferenceHolder.init(preferences)
        with(preferences.mainTelemetryAddress.split(":")) {
            factory.host = get(0)
            factory.port = getOrNull(1)?.toIntOrNull() ?: DEFAULT_PORT
        }
        startTelemetry()
        launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    if (checkActivity()) {
                        val counter = locationCounter.incrementAndGet()
                        val locationDelay = counter * config.timerInterval
                        if (locationDelay > config.locationMinDelay) {
                            onLocationAvailability(false)
                        }
                        addLoopEvent(locationDelay)
                        if (connectivityManager.isConnected) {
                            sendLastEvent()
                        }
                    }
                    delay(config.timerInterval)
                }
            }
        }
    }

    @Synchronized
    private fun addLoopEvent(delay: Long) {
        if (delay > config.locationMaxDelay) {
            lastEventTime = null
            basePoint = null
            if (!BuildConfig.PROD) {
                if (locationCounter.get() % 2 == 0L) {
                    bgToast("Не удается определить местоположение")
                }
            }
            return
        }
        val lastTime = lastEventTime ?: return
        val point = basePoint ?: return
        var eventDelay = 0L
        when (point.state) {
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
            with(preferenceHolder) {
                db.locationDao().insert(LocationEvent(point, tokenId, packageId, mileage, true).also {
                    packageId++
                    lastEventTime = it.data.whenTime
                })
                sendBroadcast(Intent(ACTION_ROUTE))
            }
        }
    }

    private fun sendLastEvent() {
        db.locationDao().getLastSendEvent()?.let {
            if (!it.location.isValid) {
                db.locationDao().delete(it.location)
                checkCount()
                return
            }
            val user = it.token.carId.toString()
            val pwd = it.token.token
            try {
                factory.apply {
                    if (connection?.isOpen != true || channel?.isOpen != true || username != user || password != pwd) {
                        abortConnection()
                        username = user
                        password = pwd
                        connection = newConnection().apply {
                            channel = createChannel()
                        }
                    }
                }
                val json = gson.toJson(it)
                Timber.d("LocationEvent: $json")
                channel?.run {
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
        locationManager.requestUpdates(config.locationInterval)
    }

    override fun stopTelemetry() {
        locationManager.removeUpdates()
        synchronized(this) {
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
                if (checkActivity()) {
                    addUpdateEvent(newLocation)
                }
            }
        }
    }

    @Synchronized
    private fun addUpdateEvent(location: SimpleLocation) {
        val point = basePoint
        if (point != null) {
            with(preferenceHolder) {
                val space = point.updateLocation(location)
                val distance: Float
                if (point.state != TelemetryState.PARKING) {
                    distance = mileage + space
                    mileage = distance
                } else {
                    distance = mileage
                }
                point.replaceWith(config)?.let { state ->
                    Timber.i("Replace state with $state")
                    basePoint = BasePoint(location, state)
                    db.locationDao().insert(LocationEvent(point, tokenId, packageId, distance).also {
                        packageId++
                        lastEventTime = it.data.whenTime
                    })
                    sendBroadcast(Intent(ACTION_ROUTE))
                }
            }
        } else {
            Timber.i("Init base point")
            basePoint = BasePoint(location)
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

    private fun checkCount() {
        sendBroadcast(Intent(ACTION_ROUTE))
        val locationCount = db.locationDao().getSendCount()
        if (locationCount <= 0) {
            sendBroadcast(Intent(ACTION_CLOUD))
        }
    }

    private fun checkActivity(): Boolean {
        return when (activityManager.getTopActivity(packageName)) {
            null, LockActivity::class.java.name, LoginActivity::class.java.name -> {
                // NOTICE without saving in persistent storage
                preferenceHolder.logout()
                abortConnection()
                stopForeground(true)
                stopSelf()
                return false
            }
            else -> true
        }
    }

    /**
     * Cannot be called on UI thread because of [android.os.NetworkOnMainThreadException]
     */
    @WorkerThread
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
        serviceJob.cancelChildren()
        preferenceHolder.save(preferences)
        stopTelemetry()
        releaseWakeLock()
        super.onDestroy()
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