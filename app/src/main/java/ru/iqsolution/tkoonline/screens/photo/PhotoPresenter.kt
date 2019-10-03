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
                fileManager.copyFile()
            }
        }
    }

    override fun saveEvent(photoEvent: PhotoEvent) {
        GlobalScope.launch(Dispatchers.IO) {
            db.photoDao().insert(photoEvent)
        }
    }
}