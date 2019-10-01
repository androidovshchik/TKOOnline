package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformsContract {

    interface Presenter {

        fun saveAccessToken()

        fun loadPlatformsTypes(refresh: Boolean)

        fun platformToJson(platform: PlatformContainers): String
    }

    interface View : IBaseView {

        fun onReceivedTypes(types: List<PhotoType>)

        fun changeMapPosition(location: SimpleLocation)

        fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>)

        fun updateMapMarkers(primary: String, secondary: String)
    }
}