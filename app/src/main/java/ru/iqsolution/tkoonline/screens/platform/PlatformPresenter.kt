package ru.iqsolution.tkoonline.screens.platform

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter : BasePresenter<PlatformContract.View>(), PlatformContract.Presenter {

    val db: Database by instance()

    override fun loadCleanEvents(kpId: Int) {
        val day = preferences.serverDay
        launch {
            val cleanEvents = withContext(Dispatchers.IO) {
                db.cleanDao().getDayKpIdEvents(day, kpId)
            }
            reference.get()?.onCleanEvents(cleanEvents)
        }
    }

    override fun loadPhotoEvents(kpId: Int) {
        val day = preferences.serverDay
        launch {
            val photoEvents = withContext(Dispatchers.IO) {
                db.photoDao().getDayKpIdEvents(day, kpId)
            }
            reference.get()?.onPhotoEvents(photoEvents)
        }
    }

    override fun saveCleanEvents(platform: PlatformContainers) {
        reference.get()?.updateCloud(platform.containers.size + 1, 0)
        val day = preferences.serverDay
        val cleanEvent = CleanEvent(platform.kpId).apply {
            tokenId = preferences.tokenId
            setFromAny(platform)
        }
        launch {
            withContext(Dispatchers.IO) {
                db.cleanDao().insertMultiple(day, cleanEvent, platform.containers)
            }
            reference.get()?.closeDetails(Activity.RESULT_OK)
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}