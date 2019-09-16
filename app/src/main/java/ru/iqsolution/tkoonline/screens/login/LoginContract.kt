package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.screens.IBaseView

interface LoginContract {

    interface ContractPresenter {

        fun getArticles()
    }

    interface ContractView : IBaseView {

        fun onQrCode(value: String)
    }
}