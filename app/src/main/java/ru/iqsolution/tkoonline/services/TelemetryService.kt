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
import org.jetbrains.anko.stopService
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.extensions.startForegroundService
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.models.SimpleLocation

class TelemetryService : BaseService(), LocationListener {

    val preferences: Preferences by instance()

    private lateinit var locationManager: LocationManager

    private var wakeLock: PowerManager.WakeLock? = null

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
        locationManager = LocationManager(applicationContext, this).apply {
            requestUpdates(applicationContext)
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onLocationState(state: LocationSettingsStates?) {}

    override fun onLocationAvailability(available: Boolean) {
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(ACTION_LOCATION).apply {
                putExtra(EXTRA_AVAILABILITY, available)
            })
    }

    override fun onLocationResult(location: SimpleLocation) {
        preferences.bulk {
            lastLat = location.latitude.toFloat()
            lastLon = location.longitude.toFloat()
            lastTime = DateTime.now().toString(PATTERN_DATETIME)
        }
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(ACTION_LOCATION).apply {
                putExtra(EXTRA_LOCATION, location)
            })
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            it.release()
            wakeLock = null
        }
    }

    override fun onDestroy() {
        locationManager.release()
        releaseWakeLock()
        super.onDestroy()
    }

    companion object {

        /**
         * @return true if service is running
         */
        fun start(context: Context): Boolean = context.run {
            return if (!activityManager.isRunning<TelemetryService>()) {
                try {
                    startForegroundService<TelemetryService>() != null
                } catch (e: SecurityException) {
                    false
                }
            } else {
                true
            }
        }

        /**
         * @return true if service is stopped
         */
        fun stop(context: Context): Boolean = context.run {
            return if (activityManager.isRunning<TelemetryService>()) {
                stopService<TelemetryService>()
            } else {
                true
            }
        }
    }
}