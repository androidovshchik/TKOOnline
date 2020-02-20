package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.app.Dialog
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import ru.iqsolution.tkoonline.extensions.makeCallback

abstract class BaseDialog(activity: Activity) : Dialog(activity), KodeinAware {

    override val kodein by closestKodein()

    inline fun <reified T> makeCallback(action: T.() -> Unit) {
        context.makeCallback(action)
    }
}