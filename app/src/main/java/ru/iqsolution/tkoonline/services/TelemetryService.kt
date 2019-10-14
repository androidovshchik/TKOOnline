package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chibatching.kotpref.bulk
import com.google.android.gms.location.LocationSettingsStates
import com.google.gson.Gson
import com.rabbitmq.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * МП должно исключать генерацию более одного события в одну секунду.
 * Данные события должны генерироваться по факту прохождения дистанции в 200 метров, повороте, остановке или начале движения,
 * а также по времени не реже чем:
 * · Для состояния стоянка - 5 минут
 * · Для состояния движения и остановка - 1 минута
 */
// https://www.rabbitmq.com/api-guide.html
class TelemetryService : BaseService(), Consumer, TelemetryListener {

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
        locationManager = LocationManager(applicationContext, this).also {
            it.requestUpdates()
        }
        //factory.setUri(preferences.telemetryUri)
        val executor = Executors.newScheduledThreadPool(1)
        timer = executor.scheduleAtFixedRate({
            // background thread
            if (activityManager.getActivities(packageName) <= 0) {
                isRunning = false
                stopForeground(true)
                stopSelf()
            }
            val lastEvent = db.locationDao().getLastSendEvent()
            /*preferences.isLoggedIn
            lastEvents.forEach {
                try {
                    factory.apply {
                        username = it.token.carId.toString()
                        password = it.token.token
                    }
                    connection = factory.newConnection()
                    connection?.isOpen
                    channel = connection?.createChannel()
                    channel.exchangeDeclare("cars", "direct", true)
                    channel.basicPublish("", QUEUE_NAME, null,)
                    channel.basicConsume()
                } catch (e: Throwable) {
                    Timber.e(e)
                }
            }*/
        }, 0L, TIMER_INTERVAL, TimeUnit.MILLISECONDS)
    }

    override fun handleDelivery(
        consumerTag: String?,
        envelope: Envelope?,
        properties: AMQP.BasicProperties?,
        body: ByteArray?
    ) {
        Timber.d("handleDelivery $consumerTag")
    }

    override fun handleRecoverOk(consumerTag: String?) {
        Timber.d("handleRecoverOk $consumerTag")
    }

    override fun handleConsumeOk(consumerTag: String?) {
        Timber.d("handleConsumeOk $consumerTag")
    }

    override fun handleShutdownSignal(consumerTag: String?, sig: ShutdownSignalException?) {
        Timber.e("handleShutdownSignal $consumerTag")
        Timber.e(sig)
    }

    override fun handleCancel(consumerTag: String?) {
        Timber.d("handleCancel $consumerTag")
    }

    override fun handleCancelOk(consumerTag: String?) {
        Timber.d("handleCancelOk $consumerTag")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.hasExtra(EXTRA_TELEMETRY_TASK)) {
                isRunning = it.getBooleanExtra(EXTRA_TELEMETRY_TASK, false)
            }
        }
        return START_STICKY
    }

    @SuppressLint("WakelockTimeout")
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name).apply {
                acquire()
            }
        }
    }

    override fun onLocationState(state: LocationSettingsStates?) {}

    override fun onLocationAvailability(available: Boolean) {
        broadcastManager.sendBroadcast(Intent(ACTION_LOCATION).apply {
            putExtra(EXTRA_SYNC_AVAILABILITY, available)
        })
    }

    /**
     * Should synchronize packageId and [lastEventTime]
     */
    override fun onLocationChanged(location: Location, satellitesCount: Int) {
        val newLocation = SimpleLocation(location).apply {
            satellites = satellitesCount
        }
        onLocationResult(newLocation)
        launch {
            withContext(Dispatchers.IO) {
                synchronized(lock) {
                    if (!isRunning) {
                        basePoint = null
                        return@synchronized
                    }
                    db.locationDao().insert(event)
                    basePoint?.let { point ->
                        preferences.mileage += point.updateLocation(newLocation)
                        point.replaceWith()?.let {
                            lastEventTime = DateTime.now()
                            val event = preferences.run {
                                LocationEvent(point, tokenId, packageId, mileage.roundToInt())
                            }
                            lastEventTime = event.data.whenTime
                            basePoint = BasePoint(newLocation, it)
                        }
                    } ?: run {
                        basePoint = BasePoint(newLocation)
                    }
                }
            }
        }
    }

    override fun onLocationResult(location: SimpleLocation) {
        preferences.bulk {
            latitude = location.latitude.toFloat()
            longitude = location.longitude.toFloat()
            locationTime = DateTime.now().toString(PATTERN_DATETIME)
        }
        broadcastManager.sendBroadcast(Intent(ACTION_LOCATION).apply {
            putExtra(EXTRA_SYNC_LOCATION, location)
        })
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            it.release()
            wakeLock = null
        }
    }

    private fun closeConnection() {
        try {
            channel?.close()
        } catch (e: Throwable) {
            Timber.e(e)
        }
        try {
            connection?.close()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    override fun onDestroy() {
        timer?.cancel(true)
        closeConnection()
        locationManager.removeUpdates()
        releaseWakeLock()
        super.onDestroy()
    }

    companion object {

        private const val TIMER_INTERVAL = 2000L

        // Для состояния стоянка - 5 минут
        private const val PARKING_DELAY = 5 * 60_000L

        // Для состояния движения и остановка - 1 минута
        private const val MOVING_DELAY = 60_000L

        /**
         * @return true if service is running
         */
        @Throws(SecurityException::class)
        fun start(context: Context, vararg params: Pair<String, Any?>): Boolean = context.run {
            if (!areGranted(*DANGER_PERMISSIONS)) {
                return false
            }
            return if (!activityManager.isRunning<TelemetryService>()) {
                startForegroundService<TelemetryService>(*params) != null
            } else {
                startService<TelemetryService>(*params) != null
            }
        }

        /**
         * Currently this shouldn't be called outside
         * @return true if service is stopped
         */
        private fun stop(context: Context): Boolean = context.run {
            if (activityManager.isRunning<TelemetryService>()) {
                return stopService<TelemetryService>()
            }
            return true
        }
    }
}