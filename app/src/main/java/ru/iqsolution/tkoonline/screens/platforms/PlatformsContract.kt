package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformsContract {

    interface Presenter {

        fun loadPlatformsTypes()

        fun sortPlatforms(secondary: List<PlatformContainers>)
    }

    interface View : IBaseView {

        fun onReceivedTypes(data: List<PhotoType>)

        fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>)

        fun changeMapPosition(latitude: Double, longitude: Double)

        fun updateMapMarkers(primary: String, secondary: String)
    }
}