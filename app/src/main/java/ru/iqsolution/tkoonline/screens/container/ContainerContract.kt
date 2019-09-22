package ru.iqsolution.tkoonline.screens.container

import ru.iqsolution.tkoonline.data.models.Container
import ru.iqsolution.tkoonline.screens.IBaseView

interface ContainerContract {

    interface Presenter {

        fun getContainer(id: Int): Container?
    }

    interface View : IBaseView
}