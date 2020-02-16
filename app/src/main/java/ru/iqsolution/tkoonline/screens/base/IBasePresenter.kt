package ru.iqsolution.tkoonline.screens.base

import kotlinx.coroutines.CoroutineScope
import org.kodein.di.KodeinAware

interface IBasePresenter<V : IBaseView> : KodeinAware, CoroutineScope {

    val isAttached: Boolean

    fun attachView(view: V)

    fun loadRoute()

    fun calculateSend()

    fun toJson(instance: Any): String

    fun <T> fromJson(json: String, tClass: Class<out T>): T

    fun detachView()
}