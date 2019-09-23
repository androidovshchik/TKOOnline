package ru.iqsolution.tkoonline.screens.container

import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.screens.IBaseView

interface ContainerContract {

    interface Presenter {

        fun getContainer(id: Int): ContainerItem?
    }

    interface View : IBaseView
}