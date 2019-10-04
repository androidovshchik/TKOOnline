package ru.iqsolution.tkoonline.screens.photo

import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.IBaseView
import java.io.File

interface PhotoContract {

    interface Presenter {

        fun initEvent(photoEvent: PhotoEvent)

        fun movePhoto(src: String, dist: String)

        fun saveEvent(photoEvent: PhotoEvent)

        fun deleteEvent(photoEvent: PhotoEvent)
    }

    interface View : IBaseView {

        var externalPhoto: File

        var internalPhoto: File

        fun takePhoto()

        fun showPhoto()

        fun closePreview(result: Int)
    }
}