package ru.iqsolution.tkoonline.screens.photo

import android.app.Application
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PhotoPresenter(application: Application) : BasePresenter<PhotoContract.View>(application),
    PhotoContract.Presenter