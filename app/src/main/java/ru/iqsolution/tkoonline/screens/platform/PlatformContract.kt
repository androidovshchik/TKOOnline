package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformContract {

    interface Presenter {

        fun parsePlatform(json: String): PlatformContainers
    }

    interface View : IBaseView
}