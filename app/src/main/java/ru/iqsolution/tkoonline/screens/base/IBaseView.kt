package ru.iqsolution.tkoonline.screens.base

import org.kodein.di.DIAware

interface IBaseView : DIAware {

    val isTouchable: Boolean

    fun setTouchable(enable: Boolean)

    fun showError(e: Throwable?)

    fun showError(message: CharSequence?)

    fun onUnhandledError(e: Throwable?) {
    }
}