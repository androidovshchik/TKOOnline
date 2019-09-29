package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.models.PhotoType

interface PlatformsContract {

    interface Presenter {

        fun loadPlatforms()
    }

    interface View {

        fun onReceivedTypes(data: List<PhotoType>)

        fun updateListMarkers(
            primary: List<PlatformContainersPhotoClean>,
            secondary: List<PlatformContainersPhotoClean>
        )

        fun updateMapMarkers(primary: String, secondary: String)

        fun changeMapPosition(latitude: Double, longitude: Double)
    }
}