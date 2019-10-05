package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.app.Dialog

open class BaseDialog(activity: Activity) : Dialog(activity) {

    inline fun <reified T> makeCallback(action: T.() -> Unit) {
        ownerActivity?.let {
            if (it is T && !it.isFinishing) {
                action(it)
            }
        }
    }
}