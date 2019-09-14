package ru.iqsolution.tkoonline.screens

import androidx.annotation.StringRes

interface IBaseView {

    fun showLoading()

    fun hideLoading()

    fun showError(message: String)

    fun showError(@StringRes id: Int)
}