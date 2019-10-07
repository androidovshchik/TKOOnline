package ru.iqsolution.tkoonline.screens.base

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    fun attachView(view: V)

    fun calculateSend()

    fun toJson(instance: Collection<Any>): String

    fun <T> toJson(instance: T, tClass: Class<out T>): String

    fun <T> fromJson(json: String, tClass: Class<out T>): T

    fun detachView()
}