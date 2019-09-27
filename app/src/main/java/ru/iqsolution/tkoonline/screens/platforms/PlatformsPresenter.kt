package ru.iqsolution.tkoonline.screens.platforms

import android.app.Application
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformsPresenter(application: Application) : BasePresenter<PlatformsContract.View>(application),
    PlatformsContract.Presenter {

    override val isAllowedPhotoKp
        get() = preferences.allowPhotoRefKp
}