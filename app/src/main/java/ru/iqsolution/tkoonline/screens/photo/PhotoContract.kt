package ru.iqsolution.tkoonline.screens.photo

import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.IBaseView
import java.io.File

interface PhotoContract {

    interface Presenter {

        fun moveFile(fileManager: FileManager, src: File, dist: File)

        fun deleteEvent(photoEvent: PhotoEvent, file: File)

        fun saveEvent(photoEvent: PhotoEvent, file: File)
    }

    interface View : IBaseView {

        fun showPhoto()

        fun closeScreen(result: Int)
    }
}