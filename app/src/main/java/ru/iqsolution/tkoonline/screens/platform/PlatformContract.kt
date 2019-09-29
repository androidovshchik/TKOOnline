package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.models.Platform
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformContract {

    interface Presenter {

        fun parsePlatform(json: String): Platform
    }

    interface View : IBaseView
}