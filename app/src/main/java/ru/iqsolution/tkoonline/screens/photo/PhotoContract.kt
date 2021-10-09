package ru.iqsolution.tkoonline.screens.photo

import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView
import java.io.File

interface PhotoContract {

    interface Presenter : IUserPresenter<View> {

        fun getExternalFile(photoEvent: PhotoEvent): File?

        fun saveEvent(photoEvent: PhotoEvent, externalFile: File)

        fun deleteEvent(photoEvent: PhotoEvent)
    }

    interface View : IUserView {

        fun closePreview(result: Int)
    }
}