package ru.iqsolution.tkoonline.screens.containers

import com.yandex.mapkit.geometry.Point
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.data.models.PhotoItem
import ru.iqsolution.tkoonline.screens.IBaseView

interface ContainersContract {

    interface Presenter {

        val isAllowedPhotoKp: Boolean

        fun receiveData()
    }

    interface View : IBaseView {

        fun onReceivedTypes(data: List<PhotoItem>)

        fun onReceivedContainers(data: List<ContainerItem>, center: Point)
    }
}