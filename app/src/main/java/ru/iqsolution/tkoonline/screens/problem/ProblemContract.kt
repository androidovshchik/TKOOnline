package ru.iqsolution.tkoonline.screens.problem

import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView

interface ProblemContract {

    interface Presenter : IUserPresenter<View>

    interface View : IUserView
}