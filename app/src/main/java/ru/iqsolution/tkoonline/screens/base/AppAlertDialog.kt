package ru.iqsolution.tkoonline.screens.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import androidx.appcompat.app.AlertDialog
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.activityCallback

private typealias OnClickListener = (dialog: DialogInterface, which: Int) -> Unit

class AppAlertDialog(context: Context) : AlertDialog(context, R.style.AlertDialog), KodeinAware {

    override val kodein by closestKodein()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    fun neutralPressed(text: CharSequence, listener: OnClickListener? = null) {
        setButton(DialogInterface.BUTTON_NEUTRAL, text, listener)
    }

    fun cancelButton(text: CharSequence = "Отмена", listener: OnClickListener? = null) {
        setButton(DialogInterface.BUTTON_NEGATIVE, text, listener)
    }

    fun positiveButton(text: CharSequence = "ОК", listener: OnClickListener? = null) {
        setButton(DialogInterface.BUTTON_POSITIVE, text, listener)
    }

    fun display(): AppAlertDialog {
        show()
        return this
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyLongPress(keyCode, event)
    }

    inline fun <reified T> activityCallback(action: T.() -> Unit) {
        context.activityCallback(action)
    }
}

inline fun Context.alert(
    message: CharSequence,
    title: CharSequence? = null,
    init: AppAlertDialog.() -> Unit
): AppAlertDialog {
    return AppAlertDialog(this).apply {
        setTitle(title)
        setMessage(message)
        init()
    }
}