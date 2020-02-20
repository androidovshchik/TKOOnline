package ru.iqsolution.tkoonline.screens.login

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView
import ru.iqsolution.tkoonline.screens.login.qrcode.ScannerListener

interface LoginContract {

    interface Presenter : IBasePresenter<View>, Observer<WorkInfo> {

        fun login(context: Context, data: String)

        fun reset()

        fun checkUpdates()

        fun export(context: Context)
    }

    interface View : IBaseView, ScannerListener, SettingsListener {

        fun onExported(success: Boolean)

        fun onLoggedIn()

        fun onCanUpdate()

        fun onUpdateAvailable()
    }
}