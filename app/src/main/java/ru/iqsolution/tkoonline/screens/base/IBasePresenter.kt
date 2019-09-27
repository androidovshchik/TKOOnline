package ru.iqsolution.tkoonline.screens.base

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    fun attachView(view: V)

    fun clearAuthorization()

    fun detachView()
}