package ru.iqsolution.tkoonline.screens.camera

import androidx.camera.core.ImageCapture
import androidx.lifecycle.LifecycleOwner
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface CameraContract {

    interface Presenter : IBasePresenter<View>

    interface View : IBaseView, LifecycleOwner, ImageCapture.OnImageSavedCallback
}