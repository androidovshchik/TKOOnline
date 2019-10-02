package ru.iqsolution.tkoonline.screens.photo

import android.app.Application
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PhotoPresenter(application: Application) : BasePresenter<PhotoContract.View>(application),
    PhotoContract.Presenter {

    val gson: Gson by instance()

    override fun platformFromJson(json: String?): PlatformContainers? {
        if (json == null) {
            return null
        }
        return gson.fromJson(json, PlatformContainers::class.java)
    }

    override fun moveFile(fileManager: FileManager) {
        launch {
            withContext(Dispatchers.IO) {
                fileManager.moveToInternal()
            }
        }
    }
}