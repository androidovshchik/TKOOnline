package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.local.entities.CleanEventRelated
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformContract {

    interface Presenter {

        fun loadCleanEvents(kpId: Int)

        fun loadPhotoEvents(kpId: Int)

        fun saveCleanEvents(platform: PlatformContainers)
    }

    interface View : IBaseView {

        fun onCleanEvents(event: CleanEventRelated?)

        fun onPhotoEvents(events: List<PhotoEvent>)

        fun closeDetails(hasChanges: Boolean)
    }
}