package ru.iqsolution.tkoonline.screens.base

import android.content.Context
import kotlinx.coroutines.*
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.entities.LocationEvent
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

    override fun loadRoute() {
        val day = preferences.serverDay
        val carId = preferences.carId
        launch {
            val locationEvents = mutableListOf<LocationEvent>()
            withContext(Dispatchers.IO) {
                locationEvents.addAll(db.locationDao().getDayCarEvents(day, carId).map { it.location })
            }
            reference.get()?.onRoute(locationEvents)
        }
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