package ru.iqsolution.tkoonline.screens.photo

import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.IBaseView
import java.io.File

interface PhotoContract {

    interface Presenter {

        fun initEvent(photoEvent: PhotoEvent): File

        fun updateEvent(photoEvent: PhotoEvent)

        fun saveEvent(photoEvent: PhotoEvent)

        fun deleteEvent(photoEvent: PhotoEvent)
    }

    interface View : IBaseView {

        fun takePhoto()

        fun showPhoto()

        fun closePreview(result: Int)
    }
}