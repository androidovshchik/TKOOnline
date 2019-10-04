package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.screens.base.IBaseView

interface LoginContract {

    interface Presenter {

        fun login(data: String)
    }

    interface View : IBaseView {

        fun onLoggedIn()
    }
}