package ru.iqsolution.tkoonline.screens.login

import android.content.Context
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface LoginContract {

    interface Presenter {

        fun export(context: Context)

        fun login(data: String)

        fun reset()
    }

    interface View : IBaseView {

        fun onExported(success: Boolean)

        fun onLoggedIn()
    }
}