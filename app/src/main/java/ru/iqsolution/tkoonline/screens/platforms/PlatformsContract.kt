package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformsContract {

    interface Presenter {

        fun loadPlatformsTypes(refresh: Boolean)
    }

    interface View : IBaseView {

        fun onReceivedTypes(types: List<PhotoType>)

        fun changeMapPosition(location: SimpleLocation)

        fun onReceivedPrimary(platforms: List<PlatformContainers>)

        fun onReceivedSecondary(platforms: List<PlatformContainers>)

        fun updateMapMarkers(primary: String, secondary: String)
    }
}