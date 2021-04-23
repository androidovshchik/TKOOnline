package ru.iqsolution.tkoonline.screens.outside

import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView

interface OutsideContract {

    interface Presenter : IUserPresenter<View>

    interface View : IUserView
}