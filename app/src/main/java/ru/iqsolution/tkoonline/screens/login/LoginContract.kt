package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.screens.base.IBaseView

interface LoginContract {

    interface Presenter {

        fun login(data: String)
    }

    interface View : IBaseView {

        /**
         * Called from [QrCodeFragment]
         */
        fun onQrCode(value: String)

        /**
         * Called from [PasswordDialog]
         */
        fun onSuccessPrompt()

        /**
         * Called from [SettingsDialog]
         */
        fun onKioskMode(enter: Boolean)

        fun onAuthorized()
    }
}