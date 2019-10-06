package ru.iqsolution.tkoonline.screens.base

interface IBaseView {

    fun updateCloud()

    fun checkLocation()

    fun showError(message: CharSequence?)
}