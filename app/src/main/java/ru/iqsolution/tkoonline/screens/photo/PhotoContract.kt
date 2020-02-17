package ru.iqsolution.tkoonline.screens.photo

import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView
import java.io.File

interface PhotoContract {

    interface Presenter : IBasePresenter<View> {

        fun getExternalFile(photoEvent: PhotoEvent): File

        fun saveEvent(photoEvent: PhotoEvent, kpIds: List<Int>, externalFile: File)

        fun deleteEvent(photoEvent: PhotoEvent)
    }

    interface View : IBaseView {

        fun closePreview(result: Int)
    }
}