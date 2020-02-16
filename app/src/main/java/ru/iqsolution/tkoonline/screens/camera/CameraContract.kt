package ru.iqsolution.tkoonline.screens.camera

import androidx.camera.core.ImageCapture
import androidx.lifecycle.LifecycleOwner
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface CameraContract {

    interface Presenter

    interface View : IBaseView, LifecycleOwner, ImageCapture.OnImageSavedCallback
}