package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.screens.IBaseView

interface LoginContract {

    interface Presenter {

        fun clearAuthorization()

        fun login(data: String)

        fun resetQrCode()
    }

    interface View : IBaseView {

        fun onSuccessPrompt()

        fun onKioskMode(enter: Boolean)

        fun onQrCode(value: String)

        fun onAuthorized()
    }
}