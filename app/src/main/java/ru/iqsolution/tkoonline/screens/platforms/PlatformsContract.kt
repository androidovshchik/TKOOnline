package ru.iqsolution.tkoonline.screens.platforms

import android.content.Context
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformsContract {

    interface Presenter {

        fun loadPlatformsTypes(refresh: Boolean)

        fun loadPhotoCleanEvents()

        fun logout(context: Context)
    }

    interface View : IBaseView {

        fun onReceivedTypes(types: List<PhotoType>)

        fun changeMapPosition(latitude: Double, longitude: Double)

        fun onReceivedPrimary(platforms: List<PlatformContainers>)

        fun onReceivedSecondary(platforms: List<PlatformContainers>)

        fun updateMapMarkers(primary: String, secondary: String)

        fun onPhotoEvents(events: List<PhotoEvent>)

        fun onCleanEvents(events: List<CleanEvent>)

        fun onLoggedOut()
    }
}