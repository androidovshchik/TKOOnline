package ru.iqsolution.tkoonline.screens.base

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.MainApp
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.services.TelemetryService
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
open class BasePresenter<V : IBaseView> : IBasePresenter<V>, KodeinAware, CoroutineScope {

    val preferences: Preferences by instance()

    val db: Database by instance()

    val gson: Gson by instance()

    protected lateinit var reference: WeakReference<V>

    protected val baseJob = SupervisorJob()

    override val isAttached: Boolean
        get() = ::reference.isInitialized

    override fun attachView(view: V) {
        reference = WeakReference(view)
    }

    override fun launchTelemetry(context: Context) {
        if (context.activityManager.isRunning<TelemetryService>()) {
            return
        }
        GlobalScope.launch(Dispatchers.Main) {
            var attempts = 0
            repeat(3) {
                if (attempts < 0) {
                    return@repeat
                }
                try {
                    TelemetryService.start(context)
                    attempts = -1
                } catch (e: Throwable) {
                    delay(3000L + attempts * 1000L)
                    attempts++
                }
            }
        }
    }

    override fun loadRoute() {
        val day = preferences.serverDay
        val carId = preferences.carId
        launch {
            val locationEvents = arrayListOf<LocationEvent>()
            withContext(Dispatchers.IO) {
                db.locationDao().getDayEvents(day).forEach {
                    if (it.token.carId == carId) {
                        locationEvents.add(it.location)
                    }
                }
            }
            reference.get()?.onRoute(locationEvents)
        }
    }

    override fun calculateSend() {
        launch {
            var photoCount = 0
            val allCount = withContext(Dispatchers.IO) {
                photoCount = db.photoDao().getSendCount()
                photoCount + db.cleanDao().getSendCount() + db.locationDao().getSendCount()
            }
            reference.get()?.updateCloud(allCount, photoCount)
        }
    }

    override fun toJson(instance: Any): String {
        return gson.toJson(instance)
    }

    override fun <T> fromJson(json: String, tClass: Class<out T>): T {
        return gson.fromJson(json, tClass)
    }

    override fun detachView() {
        baseJob.cancelChildren()
        reference.clear()
    }

    override val kodein = MainApp.instance.kodein

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        if (e !is CancellationException) {
            //todo logout
            reference.get()?.showError(e)
        }
    }
}