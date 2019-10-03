package ru.iqsolution.tkoonline.screens.base

import android.app.Application
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.MainApp
import ru.iqsolution.tkoonline.local.Preferences
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
open class BasePresenter<V : IBaseView>(application: Application) : IBasePresenter<V>, KodeinAware, CoroutineScope {

    val preferences: Preferences by instance()

    val gson: Gson by instance()

    protected lateinit var reference: WeakReference<V>

    protected val baseJob = SupervisorJob()

    override val isAttached: Boolean
        get() = ::reference.isInitialized

    override fun attachView(view: V) {
        reference = WeakReference(view)
    }

    override fun <T> toJson(instance: T, tClass: Class<out T>): String {
        return gson.toJson(tClass)
    }

    override fun <T> fromJson(json: String, tClass: Class<out T>): T {
        return gson.fromJson(json, tClass)
    }

    override fun detachView() {
        baseJob.cancelChildren()
        reference.clear()
    }

    override val kodein = (application as MainApp).kodein

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        if (e !is CancellationException) {
            reference.get()?.showError(
                if (BuildConfig.DEBUG) {
                    e.toString()
                } else {
                    e.localizedMessage
                }
            )
        }
    }
}