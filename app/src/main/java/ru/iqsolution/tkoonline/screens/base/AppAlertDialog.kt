package ru.iqsolution.tkoonline.screens.base

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import org.jetbrains.anko.activityManager
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.activity
import ru.iqsolution.tkoonline.extensions.activityCallback
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.screens.LockActivity

private typealias OnClickListener = (dialog: DialogInterface, which: Int) -> Unit

class AppAlertDialog(context: Context) : AlertDialog(context, R.style.AlertDialog), KodeinAware {

    override val kodein by closestKodein()

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

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean = context.run {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE) {
                activity()?.startActivityNoop<LockActivity>()
            }
            return true
        }
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