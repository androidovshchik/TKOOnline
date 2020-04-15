package ru.iqsolution.tkoonline.screens.platform

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter(context: Context) : BasePresenter<PlatformContract.View>(context),
    PlatformContract.Presenter {

    override fun loadLinkedPlatforms(linkedIds: List<Int>) {
        launch {
            val linkedPlatforms = withContext(Dispatchers.IO) {
                db.platformDao().getFromIds(linkedIds)
            }
            reference.get()?.onLinkedPlatforms(linkedPlatforms)
        }
    }

    override fun loadCleanEvents(kpId: Int) {
        val day = preferences.serverDay
        launch {
            val cleanEvents = withContext(Dispatchers.IO) {
                db.cleanDao().getDayKpEvents(day, kpId)
            }
            if (cleanEvents != null) {
                reference.get()?.onCleanEvents(cleanEvents)
            }
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

    override fun savePlatformEvents(
        platform: PlatformContainers,
        platforms: List<Platform>,
        clear: Boolean
    ) {
        val day = preferences.serverDay
        val tokenId = preferences.tokenId
        val cleanEvent = CleanEvent(platform.kpId, tokenId).apply {
            setFromAny(platform)
        }
        val cleanEvents = platforms.map {
            CleanEvent(it.kpId, tokenId).apply {
                linkedId = it.linkedKpId
                setFromAny(it)
            }
        }
        launch {
            withContext(Dispatchers.IO) {
                val validKpIds = db.cleanDao().insertMultiple(cleanEvent, cleanEvents)
                db.photoDao().markReadyMultiple(day, platform.allKpIds, validKpIds)
                db.platformDao().updateStatus(
                    platform.kpId,
                    if (clear) PlatformStatus.CLEANED.id else PlatformStatus.NOT_CLEANED.id
                )
            }
            reference.get()?.closeDetails(true)
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}