package ru.iqsolution.tkoonline.screens.containers

import android.app.Application
import ru.iqsolution.tkoonline.screens.BasePresenter

class ContainersPresenter(application: Application) : BasePresenter<ContainersContract.View>(application),
    ContainersContract.Presenter