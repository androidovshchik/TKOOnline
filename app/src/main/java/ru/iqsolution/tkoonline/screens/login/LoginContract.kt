package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.screens.IBaseView

interface LoginContract {

    interface Presenter {

        fun clearAuthorization()

        fun login(data: String)
    }

    interface View : IBaseView {

        fun onRemovePrompt(success: Boolean)

        fun onQrCode(value: String)

        fun onAuthorized()
    }
}