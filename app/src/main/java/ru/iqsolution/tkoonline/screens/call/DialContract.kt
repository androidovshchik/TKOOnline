package ru.iqsolution.tkoonline.screens.call

import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface DialContract {

    interface Presenter : IBasePresenter<View> {

        fun observeCalls()
    }

    interface View : IBaseView {

        fun onCallState(state: Int)

        fun finish()
    }
}