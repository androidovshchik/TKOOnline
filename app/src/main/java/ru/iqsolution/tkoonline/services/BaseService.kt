package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.app.Service
import android.os.PowerManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.anko.powerManager
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseService : Service(), KodeinAware, CoroutineScope {

    override val kodein by kodein()

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
