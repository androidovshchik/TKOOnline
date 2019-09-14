package ru.iqsolution.tkoonline.screens.dots

import ru.iqsolution.tkoonline.screens.IBaseView

interface DotsContract {

    interface ContractPresenter {

        fun getArticles()
    }

    interface ContractView : IBaseView {

        fun onArtilesReady()
    }
}