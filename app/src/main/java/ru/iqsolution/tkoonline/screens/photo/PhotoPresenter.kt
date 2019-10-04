package ru.iqsolution.tkoonline.screens.photo

import android.app.Activity
import android.app.Application
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

class PhotoPresenter(application: Application) : BasePresenter<PhotoContract.View>(application),
    PhotoContract.Presenter {

    val db: Database by instance()

    val fileManager: FileManager by instance()

    override fun getExternalFile(photoEvent: PhotoEvent): File {
        reference.get()?.run {
            if (photoEvent.id != null) {
                val internalPhoto = File(photoEvent.path)
                return File(fileManager.externalDir, internalPhoto.name)
            }
        }
        return fileManager.getRandomFile()
    }

    override fun saveEvent(photoEvent: PhotoEvent, externalFile: File) {
        launch {
            withContext(Dispatchers.IO) {
                val internalPhoto = File(fileManager.photosDir, externalFile.name)
                fileManager.copyFile(externalFile, internalPhoto)
                photoEvent.apply {
                    path = internalPhoto.path
                    whenTime = DateTime.now()
                }
                if (photoEvent.id == null) {
                    db.photoDao().insert(photoEvent)
                } else {
                    db.photoDao().update(photoEvent)
                }
            }
            reference.get()?.closePreview(Activity.RESULT_OK)
        }
    }

    override fun deleteEvent(photoEvent: PhotoEvent) {
        if (photoEvent.id != null) {
            launch {
                withContext(Dispatchers.IO) {
                    db.photoDao().delete(photoEvent)
                }
                reference.get()?.closePreview(Activity.RESULT_CANCELED)
            }
        } else {
            reference.get()?.closePreview(Activity.RESULT_CANCELED)
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }
}