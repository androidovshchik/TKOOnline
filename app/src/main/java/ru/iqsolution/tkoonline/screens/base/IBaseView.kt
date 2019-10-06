package ru.iqsolution.tkoonline.screens.base

interface IBaseView {

    fun checkLocation()

    fun updateCloud()

    fun updateCloud(hasData: Boolean, photoCount: Int)

    fun showError(message: CharSequence?)
}