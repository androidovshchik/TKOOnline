package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformsContract {

    interface Presenter {

        fun loadPlatformsTypes()
    }

    interface View : IBaseView {

        fun onReceivedTypes(data: List<PhotoType>)

        fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>)

        fun updateMapMarkers(primary: String, secondary: String)

        fun changeMapPosition(latitude: Double, longitude: Double)
    }
}