package ru.iqsolution.tkoonline.services

import android.app.Service
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseService : Service(), KodeinAware, CoroutineScope {

    override val kodein by kodein()

    protected val serviceJob = SupervisorJob()

    @Suppress("RedundantOverride")
    override fun onDestroy() {
        // no cancelling
        super.onDestroy()
    }

    override val coroutineContext = Dispatchers.Main + serviceJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}
