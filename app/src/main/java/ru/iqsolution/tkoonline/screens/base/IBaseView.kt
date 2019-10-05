package ru.iqsolution.tkoonline.screens.base

import ru.iqsolution.tkoonline.services.LocationListener

interface IBaseView : LocationListener {

    fun updateCloud(clean: Int, photo: Int)

    fun checkLocation()

    fun showLoading()

    fun hideLoading()

    fun showError(message: CharSequence?)
}