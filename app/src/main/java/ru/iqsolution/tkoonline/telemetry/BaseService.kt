package ru.iqsolution.tkoonline.telemetry

import android.annotation.SuppressLint
import android.app.Service
import android.os.PowerManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.anko.powerManager
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseService : Service(), DIAware, CoroutineScope {

    override val di by closestDI()

    protected val serviceJob = SupervisorJob()

    protected var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("WakelockTimeout")
    protected fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name).apply {
                acquire()
            }
        }
    }

    protected fun releaseWakeLock() {
        wakeLock?.let {
            it.release()
            wakeLock = null
        }
    }

    @Suppress("RedundantOverride")
    override fun onDestroy() {
        // no cancelling
        super.onDestroy()
    }

    override val coroutineContext = Dispatchers.Main + serviceJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}
