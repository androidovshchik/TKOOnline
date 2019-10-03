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

    override fun loadLastCleanEvent(id: Int) {
        launch {
            withContext(Dispatchers.IO) {
                reference.get()?.onLastCleanEvent(db.cleanDao().getDayKpIdEvent(preferences.serverDay, id))
            }
        }
    }

    override fun loadPhotoEvents(id: Int) {
        launch {
            withContext(Dispatchers.IO) {
                reference.get()?.onPhotoEvents(db.photoDao().getDayKpIdEvents(preferences.serverDay, id))
            }
        }
    }

    override fun createCleanEvents(platform: PlatformContainers) {
        reference.get()?.showLoading()
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
}