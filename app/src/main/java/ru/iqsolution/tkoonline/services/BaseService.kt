package ru.iqsolution.tkoonline.services

import android.app.Service
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import timber.log.Timber

abstract class BaseService : Service(), KodeinAware, CoroutineScope {

    override val kodein by kodein()

    val serviceJob = SupervisorJob()

    @Suppress("RedundantOverride")
    override fun onDestroy() {
        serviceJob.cancelChildren()
        super.onDestroy()
    }

    override val coroutineContext = Dispatchers.Main + serviceJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}
