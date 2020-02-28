package ru.iqsolution.tkoonline.screens.login

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView
import ru.iqsolution.tkoonline.screens.common.wait.WaitListener

interface LoginContract {

    interface Presenter : IBasePresenter<View>, Observer<WorkInfo> {

        fun login(data: String)

        fun checkUpdates()

        fun installUpdate(context: Context)

        fun exportDb(context: Context)
    }

    interface View : IBaseView, SettingsListener, WaitListener {

        fun onQrCode(value: String)

        fun onLoggedIn()

        fun onCanUpdate()

        fun onUpdateAvailable()

        fun onUpdateEnd(success: Boolean)

        fun onExportedDb(success: Boolean)
    }
}