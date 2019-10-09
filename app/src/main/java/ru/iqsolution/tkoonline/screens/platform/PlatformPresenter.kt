package ru.iqsolution.tkoonline.screens.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.models.SimpleContainer
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter : BasePresenter<PlatformContract.View>(), PlatformContract.Presenter {

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

    override fun saveCleanEvents(cleanEvent: CleanEvent, containers: List<SimpleContainer>) {
        val day = preferences.serverDay
        launch {
            reference.get()?.apply {
                updateCloud()
                closeDetails(true)
            }
            withContext(Dispatchers.IO) {
                db.cleanDao().insertMultiple(day, cleanEvent, containers)
            }
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}