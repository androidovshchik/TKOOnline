package ru.iqsolution.tkoonline.screens

import android.app.Application
import com.chibatching.kotpref.bulk
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.MainApplication
import ru.iqsolution.tkoonline.data.local.Preferences
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
open class BasePresenter<V : IBaseView>(application: Application) : IBasePresenter<V>, KodeinAware, CoroutineScope {

    val preferences: Preferences by instance()

    protected lateinit var viewRef: WeakReference<V>

    protected val baseJob = SupervisorJob()

    override val isAttached: Boolean
        get() = ::viewRef.isInitialized

    override fun attachView(view: V) {
        viewRef = WeakReference(view)
    }

    override fun clearAuthorization() {
        preferences.bulk {
            accessToken = null
            expiresToken = null
            allowPhotoRefKp = false
            serverTime = null
        }
    }

    override fun detachView() {
        baseJob.cancelChildren()
        viewRef.clear()
    }

    override val kodein = (application as MainApplication).kodein

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}