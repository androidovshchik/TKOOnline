package ru.iqsolution.tkoonline.screens.outside

import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface OutsideContract {

    interface Presenter : IBasePresenter<View>

    interface View : IBaseView
}