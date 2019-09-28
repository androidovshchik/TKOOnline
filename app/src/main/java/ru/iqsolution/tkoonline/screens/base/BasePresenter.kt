package ru.iqsolution.tkoonline.screens.base

import android.app.Application
import com.chibatching.kotpref.bulk
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.MainApp
import ru.iqsolution.tkoonline.local.AppDatabase
import ru.iqsolution.tkoonline.local.Preferences
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
open class BasePresenter<V : IBaseView>(application: Application) : IBasePresenter<V>, KodeinAware, CoroutineScope {

    val appDb: AppDatabase by instance()

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
            expiresWhen = null
            allowPhotoRefKp = false
            serverTime = null
            elapsedTime = 0L
            vehicleNumber = null
            queName = null
            carId = 0
        }
    }

    override fun detachView() {
        baseJob.cancelChildren()
        viewRef.clear()
    }

    override val kodein = (application as MainApp).kodein

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}