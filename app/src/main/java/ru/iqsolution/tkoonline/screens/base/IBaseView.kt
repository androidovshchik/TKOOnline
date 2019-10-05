package ru.iqsolution.tkoonline.screens.base

interface IBaseView {

    fun updateCloud(clean: Int, photo: Int)

    fun checkLocation()

    fun showLoading()

    fun hideLoading()

    fun showError(message: CharSequence?)
}