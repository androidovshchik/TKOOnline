package ru.iqsolution.tkoonline.screens.base

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    fun attachView(view: V)

    fun calculateSend()

    fun toJson(instance: Any): String

    fun <T> fromJson(json: String, tClass: Class<out T>): T

    fun detachView()
}