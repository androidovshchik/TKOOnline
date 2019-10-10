package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chibatching.kotpref.bulk
import com.google.android.gms.location.LocationSettingsStates
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
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.models.SimpleLocation
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class TelemetryService : BaseService(), TelemetryListener, LocationListener {

    val preferences: Preferences by instance()

    private lateinit var locationManager: LocationManager

    private lateinit var broadcastManager: LocalBroadcastManager

    private var wakeLock: PowerManager.WakeLock? = null

    private var timer: ScheduledFuture<*>? = null

    @Volatile
    private var isRunning = false

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
        val executor = Executors.newScheduledThreadPool(1)
        timer = executor.scheduleAtFixedRate({
            Timber.d(">>>>> scheduleAtFixedRate ${activityManager.getActivities(packageName)}")
            if (activityManager.getActivities(packageName) <= 0) {
                stopTelemetry()
                stopForeground(true)
                stopSelf()
            }
            /*val factory = ConnectionFactory().apply {
                this.isAutomaticRecoveryEnabled
            }
            factory.host = "localhost"
            factory.newConnection("").use { connection ->
                connection.createChannel().use { channel ->
                    channel.basicPublish()
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null)
                    val message = "Hello World!"
                    channel.basicPublish("", QUEUE_NAME, null, message.toByteArray())
                    println(" [x] Sent '$message'")
                }
            }*/
        }, 0, LOCATION_INTERVAL, TimeUnit.MILLISECONDS)
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

    @SuppressLint("WakelockTimeout")
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name).apply {
                acquire()
            }
        }
    }

    override fun startTelemetry() {
        isRunning = true
    }

    override fun stopTelemetry() {
        isRunning = false
    }

    override fun onLocationState(state: LocationSettingsStates?) {}

    override fun onLocationAvailability(available: Boolean) {
        broadcastManager.sendBroadcast(Intent(ACTION_LOCATION).apply {
            putExtra(EXTRA_SYNC_AVAILABILITY, available)
        })
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

    override fun onDestroy() {
        //channel.close()
        //conn.close()
        timer?.cancel(true)
        locationManager.removeUpdates()
        releaseWakeLock()
        super.onDestroy()
    }

    companion object {

        // Для состояния стоянка - 5 минут
        private const val PARKING_DELAY = 5 * 60_000L

        // Для состояния движения и остановка - 1 минута
        private const val MOVING_DELAY = 60_000L

        /**
         * Событие стоянка
         * Данное событие генерируется в состоянии остановка если данное состояние не изменено в течение 2 минут
         */
        private const val PARKING_TIME = 2 * 60_000L

        // Направление движения отклоняется от базового на величину 5 градусов
        private const val BASE_DEGREE = 5

        // Скорость выше параметра минимальной скорости (10км/ч)
        private const val MIN_SPEED = 10

        // минимальное время - 30 секунд
        private const val MIN_TIME = 30

        /**
         * Событие пройдена дистанция
         * Данное событие генерируется только в состоянии движения при перемещении автомобиля от базовой точки на расстояние больше 200 метров.
         */
        private const val BASE_DISTANCE = 200

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