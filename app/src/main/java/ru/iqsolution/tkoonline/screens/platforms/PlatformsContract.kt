package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PlatformsContract {

    interface Presenter {

        val isAllowedPhotoKp: Boolean
    }

    interface View : IBaseView
}