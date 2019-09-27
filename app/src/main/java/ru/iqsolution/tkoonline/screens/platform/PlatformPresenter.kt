package ru.iqsolution.tkoonline.screens.platform

import android.app.Application
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter(application: Application) : BasePresenter<PlatformContract.View>(application),
    PlatformContract.Presenter