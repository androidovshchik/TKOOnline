package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.screens.IBaseView

interface LoginContract {

    interface ContractPresenter {

        fun login(data: String)
    }

    interface ContractView : IBaseView {

        fun onQrCode(value: String)
    }
}