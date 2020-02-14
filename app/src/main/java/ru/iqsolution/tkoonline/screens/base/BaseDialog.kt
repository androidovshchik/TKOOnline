package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.app.Dialog
import ru.iqsolution.tkoonline.extensions.makeCallback

abstract class BaseDialog(activity: Activity) : Dialog(activity) {

    inline fun <reified T> makeCallback(action: T.() -> Unit) {
        context.makeCallback(action)
    }
}