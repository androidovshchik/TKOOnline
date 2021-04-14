package ru.iqsolution.tkoonline.screens.base

import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DIAware

interface IBasePresenter<V : IBaseView> : DIAware, CoroutineScope {

    val isAttached: Boolean

    fun attachView(view: V)

    fun calculateSend()

    fun loadRoute()

    fun detachView()
}