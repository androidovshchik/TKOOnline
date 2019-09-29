package ru.iqsolution.tkoonline.screens.login

interface LoginContract {

    interface Presenter {

        fun login(data: String)
    }

    interface View {

        /**
         * Called from [PasswordDialog]
         */
        fun onPrompted()

        /**
         * Called from [SettingsDialog]
         */
        fun onKioskMode(enter: Boolean)

        fun onAuthorized()
    }
}