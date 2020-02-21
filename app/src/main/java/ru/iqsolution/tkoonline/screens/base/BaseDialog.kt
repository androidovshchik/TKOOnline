package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import ru.iqsolution.tkoonline.extensions.makeCallback

abstract class BaseDialog(activity: Activity) : Dialog(activity), KodeinAware {

    override val kodein by closestKodein()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    inline fun <reified T> makeCallback(action: T.() -> Unit) {
        context.makeCallback(action)
    }
}