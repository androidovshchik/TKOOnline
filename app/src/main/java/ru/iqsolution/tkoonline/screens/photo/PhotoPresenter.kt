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

    override fun initEvent(photoEvent: PhotoEvent) {
        reference.get()?.apply {
            if (photoEvent.id != null) {
                internalPhoto = File(photoEvent.path)
                externalPhoto = File(fileManager.externalDir, internalPhoto.name)
                showPhoto()
            } else {
                externalPhoto = fileManager.getRandomFile()
                internalPhoto = File(fileManager.photosDir, externalPhoto.name)
                takePhoto()
            }
        }
    }

    override fun movePhoto(src: String, dist: String) {
        launch {
            withContext(Dispatchers.IO) {
                fileManager.apply {
                    copyFile(src, dist)
                    deleteFile(src)
                }
            }
            reference.get()?.showPhoto()
        }
    }

    override fun saveEvent(photoEvent: PhotoEvent) {
        if (photoEvent.id == null) {
            launch {
                withContext(Dispatchers.IO) {
                    photoEvent.id = db.photoDao().insert(photoEvent)
                }
                reference.get()?.closePreview(Activity.RESULT_OK)
            }
        } else {
            reference.get()?.closePreview(Activity.RESULT_CANCELED)
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