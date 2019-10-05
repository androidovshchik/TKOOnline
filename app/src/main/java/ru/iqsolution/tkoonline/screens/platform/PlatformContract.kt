package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformContract {

    interface Presenter {

        fun loadLastCleanEvent(kpId: Int)

        fun loadPhotoEvents(kpId: Int)

        fun saveCleanEvents(platform: PlatformContainers)
    }

    interface View : IBaseView {

        fun onLastCleanEvent(event: CleanEvent?)

        fun onPhotoEvents(events: List<PhotoEvent>)

        fun closeDetails(result: Int)
    }
}