package ru.iqsolution.tkoonline.screens.problem

import android.app.Application
import ru.iqsolution.tkoonline.screens.BasePresenter

class ProblemPresenter(application: Application) : BasePresenter<ProblemContract.View>(application),
    ProblemContract.Presenter