package ru.iqsolution.tkoonline.screens.photo

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import java.io.File

class PhotoPresenter : BasePresenter<PhotoContract.View>(), PhotoContract.Presenter {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    override fun getExternalFile(photoEvent: PhotoEvent): File {
        if (photoEvent.id != null) {
            val internalPhoto = File(photoEvent.path)
            return File(fileManager.externalDir, internalPhoto.name)
        }
        return fileManager.getRandomFile()
    }

    /**
     * Normally there will be no case when file doesn't exist and id is null
     */
    override fun saveEvent(photoEvent: PhotoEvent, linkedIds: List<Int>, externalFile: File) {
        val internalPhoto = File(fileManager.photosDir, externalFile.name)
        photoEvent.apply {
            tokenId = preferences.tokenId
            latitude = preferences.lastLatitude.toDouble()
            longitude = preferences.lastLongitude.toDouble()
            // it may be empty when there is a new event
            path = internalPhoto.path
            whenTime = DateTime.now()
        }
        launch {
            withContext(Dispatchers.IO) {
                fileManager.copyFile(externalFile, internalPhoto)
                if (photoEvent.id == null) {
                    if (photoEvent.kpId == null) {
                        db.photoDao().insert(photoEvent)
                    } else {
                        db.photoDao().insertMultiple(photoEvent, linkedIds)
                    }
                } else {
                    // NOTICE events without kp id have no updates
                    db.photoDao().updateMultiple(photoEvent)
                }
            }
            reference.get()?.apply {
                updateCloud()
                closePreview(Activity.RESULT_OK)
            }
        }
    }

    override fun deleteEvent(photoEvent: PhotoEvent) {
        if (photoEvent.id == null) {
            // there is nothing to delete
            reference.get()?.closePreview(Activity.RESULT_CANCELED)
            return
        }
        launch {
            withContext(Dispatchers.IO) {
                db.photoDao().delete(photoEvent)
            }
            reference.get()?.closePreview(Activity.RESULT_CANCELED)
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}