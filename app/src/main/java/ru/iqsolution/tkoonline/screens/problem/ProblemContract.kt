package ru.iqsolution.tkoonline.screens.problem

import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface ProblemContract {

    interface Presenter : IBasePresenter<View>

    interface View : IBaseView
}