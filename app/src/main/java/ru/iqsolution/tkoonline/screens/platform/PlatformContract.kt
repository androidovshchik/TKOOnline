package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformContract {

    interface Presenter {

        fun loadPhotoCleanEvents(platform: PlatformContainers)
    }

    interface View : IBaseView {

        fun onPhotoEvents(events: List<PhotoEvent>)

        fun onCleanEvents(events: List<CleanEvent>)
    }
}