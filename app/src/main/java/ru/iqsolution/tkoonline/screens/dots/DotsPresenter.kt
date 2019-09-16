package ru.iqsolution.tkoonline.screens.dots

import android.app.Application
import ru.iqsolution.tkoonline.screens.BasePresenter

class DotsPresenter(application: Application) : BasePresenter<DotsContract.ContractView>(application),
    DotsContract.ContractPresenter