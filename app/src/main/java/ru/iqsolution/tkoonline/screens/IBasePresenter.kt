package ru.iqsolution.tkoonline.screens

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    fun attachView(view: V)

    fun detachView()
}