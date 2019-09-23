package ru.iqsolution.tkoonline.screens.photo

import android.app.Application
import ru.iqsolution.tkoonline.screens.BasePresenter

class PhotoPresenter(application: Application) : BasePresenter<PhotoContract.View>(application),
    PhotoContract.Presenter