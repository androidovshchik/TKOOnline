package ru.iqsolution.tkoonline.screens

import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
open class BasePresenter<V : IBaseView> : IBasePresenter<V>, CoroutineScope {

    protected lateinit var viewRef: WeakReference<V>

    protected val baseJob = SupervisorJob()

    override val isAttached: Boolean
        get() = ::viewRef.isInitialized

    override fun attachView(view: V) {
        viewRef = WeakReference(view)
    }

    override fun detachView() {
        baseJob.cancelChildren()
        viewRef.clear()
    }

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}