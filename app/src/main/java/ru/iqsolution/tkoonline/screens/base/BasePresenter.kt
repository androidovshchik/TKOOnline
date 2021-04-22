package ru.iqsolution.tkoonline.screens.base

import android.content.Context
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.Preferences
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
abstract class BasePresenter<V : IBaseView>(context: Context) : IBasePresenter<V> {

    override val di by closestDI(context)

    protected val preferences: Preferences by instance()

    protected val db: Database by instance()

    protected lateinit var reference: WeakReference<V>

    protected val baseJob = SupervisorJob()

    override val isAttached: Boolean
        get() = ::reference.isInitialized

    override fun attachView(view: V) {
        reference = WeakReference(view)
    }

    override fun detachView() {
        baseJob.cancelChildren()
        reference.clear()
    }

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        reference.get()?.onUnhandledError(e)
    }
}