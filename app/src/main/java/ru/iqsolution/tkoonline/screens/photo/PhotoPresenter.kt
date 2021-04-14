package ru.iqsolution.tkoonline.screens.photo

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.kodein.di.instance
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import java.io.File

class PhotoPresenter(context: Context) : BasePresenter<PhotoContract.View>(context), PhotoContract.Presenter {

    private val fileManager: FileManager by instance()

    override fun getExternalFile(photoEvent: PhotoEvent): File? {
        val externalDir = fileManager.externalDir
        return if (externalDir != null) {
            File(externalDir, if (photoEvent.id != null) File(photoEvent.path).name else fileManager.randomName)
        } else {
            reference.get()?.closePreview(Activity.RESULT_CANCELED)
            null
        }
    }

    /**
     * Normally there will be no case when file doesn't exist and id is null
     */
    override fun saveEvent(photoEvent: PhotoEvent, linkedIds: List<Int>, externalFile: File) {
        val internalPhoto = File(fileManager.photosDir, externalFile.name)
        photoEvent.apply {
            preferences.also {
                tokenId = it.tokenId
                latitude = it.latitude.toDouble()
                longitude = it.longitude.toDouble()
            }
            // previously it may be empty when there is a new event
            path = internalPhoto.path
            whenTime = DateTime.now()
        }
        launch {
            withContext(Dispatchers.IO) {
                fileManager.apply {
                    copyImage(externalFile, internalPhoto)
                    deleteFile(externalFile)
                }
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
            reference.get()?.closePreview(Activity.RESULT_OK)
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
            reference.get()?.closePreview(Activity.RESULT_OK)
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}