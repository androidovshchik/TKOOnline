package ru.iqsolution.tkoonline.screens.login

import android.content.Context
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView
import ru.iqsolution.tkoonline.screens.login.qrcode.ScannerListener

interface LoginContract {

    interface Presenter : IBasePresenter<View> {

        fun export(context: Context)

        fun login(data: String)

        fun reset()
    }

    interface View : IBaseView, ScannerListener, SettingsListener {

        fun onExported(success: Boolean)

        fun onLoggedIn()
    }
}