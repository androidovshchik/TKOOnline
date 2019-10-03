package ru.iqsolution.tkoonline.screens.base

import ru.iqsolution.tkoonline.services.LocationListener

interface IBaseView : LocationListener {

    fun showLoading()

    fun hideLoading()

    fun showError(message: CharSequence?)

    fun checkLocation()
}