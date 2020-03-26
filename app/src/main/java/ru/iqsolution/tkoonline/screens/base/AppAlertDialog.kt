package ru.iqsolution.tkoonline.screens.base

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
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

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyLongPress(keyCode, event)
    }

    fun leftButton(text: CharSequence, listener: OnClickListener? = null) {
        setButton(DialogInterface.BUTTON_NEUTRAL, text, listener)
    }

    fun middleButton(text: CharSequence = "Отмена", listener: OnClickListener? = null) {
        setButton(DialogInterface.BUTTON_NEGATIVE, text, listener)
    }

    fun rightButton(text: CharSequence = "Ок", listener: OnClickListener? = null) {
        setButton(DialogInterface.BUTTON_POSITIVE, text, listener)
    }

    fun show(): AppAlertDialog {
        super.show()
        return this
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