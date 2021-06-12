package ru.iqsolution.tkoonline.screens.platform

import android.net.Uri
import ru.iqsolution.tkoonline.local.entities.CleanEventRelated
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.local.entities.TagEvent
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView

interface PlatformContract {

    interface Presenter : IUserPresenter<View> {

        fun generateSignature(lat: Double, lon: Double)

        fun loadLinkedPlatforms(linkedIds: List<Int>)

        fun loadCleanEvents(kpId: Int)

        fun loadPhotoEvents(kpId: Int)

        fun observeTagEvents(kpId: Int)

        fun saveTagEvent(event: TagEvent)

        fun savePlatformEvents(platform: PlatformContainers, platforms: List<Platform>, clear: Boolean)
    }

    interface View : IUserView, GalleryListener {

        var signature: Uri?

        fun onLinkedPlatforms(platforms: List<Platform>)

        fun onCleanEvents(event: CleanEventRelated)

        fun onPhotoEvents(events: List<PhotoEvent>)

        fun onTagEvents(events: List<TagEvent>)

        fun closeDetails(hasCleanChanges: Boolean)
    }
}