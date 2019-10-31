package ru.iqsolution.tkoonline.screens.base

interface IBaseView {

    fun updateRoute()

    fun updateCloud()

    fun updateCloud(allCount: Int, photoCount: Int)

    fun showError(e: Throwable?)

    fun showError(message: CharSequence?)
}