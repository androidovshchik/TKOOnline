package ru.iqsolution.tkoonline.screens.platform

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter(context: Context) : BasePresenter<PlatformContract.View>(context), PlatformContract.Presenter {

    override fun loadLinkedPlatforms(linkedIds: List<Int>) {
        launch {
            val platforms = withContext(Dispatchers.IO) {
                db.platformDao().getFromIds(linkedIds)
            }
            reference.get()?.onLinkedPlatforms(platforms)
        }
    }

    override fun loadCleanEvents(kpId: Int) {
        val day = preferences.serverDay
        launch {
            val cleanEvents = withContext(Dispatchers.IO) {
                db.cleanDao().getDayKpEvents(day, kpId)
            }
            reference.get()?.onCleanEvents(cleanEvents)
        }
    }

    override fun loadPhotoEvents(kpId: Int) {
        val day = preferences.serverDay
        launch {
            val photoEvents = withContext(Dispatchers.IO) {
                db.photoDao().getDayKpEvents(day, kpId)
            }
            reference.get()?.onPhotoEvents(photoEvents)
        }
    }

    override fun savePlatformEvents(platforms: List<Platform>) {
        val day = preferences.serverDay
        val cleanEvent = CleanEvent(platform.kpId).apply {
            tokenId = preferences.tokenId
            setFromAny(platform)
        }
        launch {
            withContext(Dispatchers.IO) {
                db.cleanDao().insertMultiple(day, cleanEvent, platform.containers)
                db.photoDao().markAsReady(day, cleanEvent.kpId)
            }
            reference.get()?.closeDetails(true)
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}