package ru.iqsolution.tkoonline.screens

import kotlinx.coroutines.*
import timber.log.Timber

open class BasePresenter<V : IBaseView> : IBasePresenter<V>, CoroutineScope {

    protected var view: V? = null

    protected val baseJob = SupervisorJob()

    override val isAttached: Boolean
        get() = view != null

    override fun attachView(view: V) {
        this.view = view
    }

    override fun detachView() {
        baseJob.cancelChildren()
        view = null
    }

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}