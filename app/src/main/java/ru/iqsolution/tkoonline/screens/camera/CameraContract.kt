package ru.iqsolution.tkoonline.screens.camera

import androidx.camera.core.ImageCapture
import androidx.lifecycle.LifecycleOwner
import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView

interface CameraContract {

    interface Presenter : IUserPresenter<View>

    interface View : IUserView, LifecycleOwner, ImageCapture.OnImageSavedCallback
}