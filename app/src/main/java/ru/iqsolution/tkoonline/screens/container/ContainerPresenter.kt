package ru.iqsolution.tkoonline.screens.container

import android.app.Application
import ru.iqsolution.tkoonline.screens.BasePresenter

class ContainerPresenter(application: Application) : BasePresenter<ContainerContract.View>(application),
    ContainerContract.Presenter