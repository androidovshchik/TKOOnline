package ru.iqsolution.tkoonline.screens.base

import kotlinx.coroutines.CoroutineScope
import org.kodein.di.KodeinAware

interface IBasePresenter<V : IBaseView> : KodeinAware, CoroutineScope {

    val isAttached: Boolean

    fun attachView(view: V)

    fun calculateSend()

    fun loadRoute()

    fun detachView()
}