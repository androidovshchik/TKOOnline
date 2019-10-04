package ru.iqsolution.tkoonline.screens.photo

import android.app.Activity
import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    /**
     * @return external file
     */
    override fun initEvent(photoEvent: PhotoEvent): File {
        var externalPhoto: File? = null
        reference.get()?.apply {
            if (photoEvent.id != null) {
                val internalPhoto = File(photoEvent.path)
                externalPhoto = File(fileManager.externalDir, internalPhoto.name)
                showPhoto(internalPhoto)
            } else {
                externalPhoto = fileManager.getRandomFile().also {
                    val internalPhoto = File(fileManager.photosDir, it.name)
                    photoEvent.path = internalPhoto.path
                    takePhoto(it)
                }
            }
        }
        return externalPhoto ?: fileManager.getRandomFile()
    }

    override fun saveEvent(photoEvent: PhotoEvent, externalFile: File) {
        launch {
            withContext(Dispatchers.IO) {
                fileManager.apply {
                    copyFile(src, dist)
                    deleteFile(src)
                }
            }
            reference.get()?.showPhoto()
        }
        db.photoDao().update(photoEvent)
        if (photoEvent.id == null) {
            launch {
                withContext(Dispatchers.IO) {
                    db.photoDao().insert(photoEvent.apply {
                    })
                }
                reference.get()?.closePreview(Activity.RESULT_OK)
            }
        } else {
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