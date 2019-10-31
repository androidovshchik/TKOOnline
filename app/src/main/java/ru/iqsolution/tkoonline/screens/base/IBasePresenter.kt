package ru.iqsolution.tkoonline.screens.base

import android.content.Context

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    fun attachView(view: V)

    fun launchTelemetry(context: Context)

    fun loadRoute()

    fun calculateSend()

    fun toJson(instance: Any): String

    fun <T> fromJson(json: String, tClass: Class<out T>): T

    fun detachView()
}