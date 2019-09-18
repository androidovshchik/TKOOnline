package ru.iqsolution.tkoonline.screens.login

import android.app.Activity
import ru.iqsolution.tkoonline.screens.IBaseView

interface LoginContract {

    interface Presenter {

        fun clearAuthorization()

        fun setKioskMode(activity: Activity, enable: Boolean?)

        fun login(data: String)
    }

    interface View : IBaseView {

        fun onSuccessPrompt()

        fun onKioskMode(enter: Boolean?)

        fun onQrCode(value: String)

        fun onAuthorized()
    }
}