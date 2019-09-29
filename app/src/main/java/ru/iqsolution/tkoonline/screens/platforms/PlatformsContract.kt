package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.local.entities.PlatformContainersPhotoClean
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformsContract {

    interface Presenter {

        val isAllowedPhotoKp: Boolean

        fun loadPlatforms()
    }

    interface View : IBaseView {

        fun updateListMarkers(
            primary: List<PlatformContainersPhotoClean>,
            secondary: List<PlatformContainersPhotoClean>
        )

        fun updateMapMarkers(primary: String, secondary: String)

        fun changeMapPosition(latitude: Double, longitude: Double)
    }
}