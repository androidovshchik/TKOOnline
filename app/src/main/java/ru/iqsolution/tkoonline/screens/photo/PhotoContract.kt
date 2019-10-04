package ru.iqsolution.tkoonline.screens.photo

import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.IBaseView
import java.io.File

interface PhotoContract {

    interface Presenter {

        fun initEvent(photoEvent: PhotoEvent): File

        fun saveEvent(photoEvent: PhotoEvent, externalFile: File)

        fun deleteEvent(photoEvent: PhotoEvent)
    }

    interface View : IBaseView {

        fun closePreview(result: Int)
    }
}