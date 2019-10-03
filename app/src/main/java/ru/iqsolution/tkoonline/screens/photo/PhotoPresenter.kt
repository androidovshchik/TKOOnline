package ru.iqsolution.tkoonline.screens.photo

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

    override fun moveFile(fileManager: FileManager, src: File, dist: File) {
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

    override fun deleteEvent(photoEvent: PhotoEvent, file: File) {
        if (photoEvent.id != null) {

        }
    }

    override fun saveEvent(photoEvent: PhotoEvent, file: File) {
        GlobalScope.launch(Dispatchers.IO) {
            db.photoDao().insert(photoEvent)
        }
    }
}