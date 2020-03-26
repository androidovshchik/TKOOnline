package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.view.KeyEvent
import org.jetbrains.anko.activityManager
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.activity
import ru.iqsolution.tkoonline.extensions.activityCallback
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.screens.LockActivity

open class BaseDialog(activity: Activity) : Dialog(activity, R.style.AppDialog), KodeinAware {

    override val kodein by closestKodein()

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