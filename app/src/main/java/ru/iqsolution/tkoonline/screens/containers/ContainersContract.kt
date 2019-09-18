package ru.iqsolution.tkoonline.screens.containers

import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.data.models.PhotoItem
import ru.iqsolution.tkoonline.screens.IBaseView

interface ContainersContract {

    interface Presenter {

        fun receiveData()
    }

    interface View : IBaseView {

        fun onReceivedTypes(data: List<PhotoItem>)

        fun onReceivedContainers(data: List<ContainerItem>)
    }
}