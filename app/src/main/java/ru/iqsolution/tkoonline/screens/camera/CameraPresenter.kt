package ru.iqsolution.tkoonline.screens.camera

import android.content.Context
import ru.iqsolution.tkoonline.screens.base.user.UserPresenter

class CameraPresenter(context: Context) : UserPresenter<CameraContract.View>(context),
    CameraContract.Presenter