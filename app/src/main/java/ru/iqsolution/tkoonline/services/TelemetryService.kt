package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.Job
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.powerManager
import org.jetbrains.anko.stopService
import ru.iqsolution.tkoonline.R

class TelemetryService : BaseService() {

    private val binder = Binder()

    private var job: Job? = null

    private lateinit var locationClient: FusedLocationProviderClient

    private lateinit var locationHandler: LocationHandler

    private val locationRequest = LocationRequest.create().apply {
        interval = 5_000L
        fastestInterval = 5_000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        return true
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(
            Int.MAX_VALUE, NotificationCompat.Builder(applicationContext, Behavior.SOUNDLESS.name)
                .setSmallIcon(R.drawable.ic_schedule_white_24dp)
                .setContentTitle("Фоновой сервис")
                .setOngoing(true)
                .build()
        )
        registerReceiver(receiver, IntentFilter().apply {
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        })
        FusedLocationProviderClient
    }

    @SuppressLint("WakelockTimeout")
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock =
                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name).apply {
                    acquire()
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun onLocationChange(location: Location) {

    }

    fun onLocationUnavailable() {

    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            it.release()
            wakeLock = null
        }
    }

    override fun onDestroy() {
        releaseWakeLock()
        super.onDestroy()
    }

    @Suppress("unused")
    inner class Binder : android.os.Binder() {

        val service: TelemetryService
            get() = this@TelemetryService
    }

    companion object {

        /**
         * @param params might not be empty
         * @return true if service is running
         */
        fun launch(preferences: Preferences, vararg params: Pair<String, Any?>): Boolean = preferences.run {
            return if (enableTasksService) {
                if (context.activityManager.isRunning<TelemetryService>()) {
                    if (params.isNotEmpty()) {
                        context.startService<TelemetryService>(*params) != null
                    } else {
                        true
                    }
                } else {
                    try {
                        context.startForegroundService<TelemetryService>(*params) != null
                    } catch (e: SecurityException) {
                        false
                    }
                }
            } else {
                !kill(context)
            }
        }

        /**
         * @return true if service is stopped
         */
        fun kill(context: Context): Boolean = context.run {
            notificationManager.cancelAll()
            return if (activityManager.isRunning<TelemetryService>()) {
                stopService<TelemetryService>()
            } else {
                true
            }
        }
    }
}