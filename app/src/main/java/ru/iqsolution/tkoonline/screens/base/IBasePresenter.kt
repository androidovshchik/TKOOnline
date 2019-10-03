package ru.iqsolution.tkoonline.screens.base

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    fun attachView(view: V)

    fun <T> toJson(instance: T, clss: Class<out T>): String

    fun <T> fromJson(json: String, clss: Class<out T>): T

    fun clearAuthorization()

    fun detachView()
}