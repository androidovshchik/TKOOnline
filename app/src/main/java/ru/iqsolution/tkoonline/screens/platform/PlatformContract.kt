package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.local.entities.CleanEventRelated
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformContract {

    interface Presenter : IBasePresenter<View> {

        fun loadLinkedPlatforms(linkedIds: List<Int>)

        fun loadCleanEvents(kpId: Int)

        fun loadPhotoEvents(kpId: Int)

        fun savePlatformEvents(platform: PlatformContainers, platforms: List<Platform>)
    }

    interface View : IBaseView, ConfirmListener, GalleryListener {

        fun onLinkedPlatforms(platforms: List<Platform>)

        fun onCleanEvents(event: CleanEventRelated)

        fun onPhotoEvents(events: List<PhotoEvent>)
    }
}