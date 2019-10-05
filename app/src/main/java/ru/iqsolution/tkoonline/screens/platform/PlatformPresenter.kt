package ru.iqsolution.tkoonline.screens.platform

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter(application: Application) : BasePresenter<PlatformContract.View>(application),
    PlatformContract.Presenter {

    val db: Database by instance()

    override fun loadLastCleanEvent(kpId: Int) {
        launch {
            val cleanEvent = withContext(Dispatchers.IO) {
                db.cleanDao().getDayKpIdEvent(preferences.serverDay, kpId)
            }
            reference.get()?.onLastCleanEvent(cleanEvent)
        }
    }

    override fun loadPhotoEvents(kpId: Int) {
        launch {
            val photoEvents = withContext(Dispatchers.IO) {
                db.photoDao().getDayKpIdEvents(preferences.serverDay, kpId)
            }
            reference.get()?.onPhotoEvents(photoEvents)
        }
    }

    override fun saveCleanEvents(platform: PlatformContainers) {
        launch {
            try {
                withContext(Dispatchers.IO) {
                    db.cleanDao().insert()
                }
                // todo launch worker with listener
            } catch (e: Throwable) {
                reference.get()?.hideLoading()
                throw e
            }
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}