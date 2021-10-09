package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.view.KeyEvent
import org.jetbrains.anko.activityManager
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.doActivityCallback
import ru.iqsolution.tkoonline.extensions.getActivity
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.screens.LockActivity

open class AppDialog(activity: Activity) : Dialog(activity, R.style.AppDialog), DIAware {

    override val di by closestDI()

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean = context.run {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE) {
                getActivity()?.startActivityNoop<LockActivity>()
            }
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    inline fun <reified T> activityCallback(action: T.() -> Unit) {
        context.doActivityCallback(action)
    }
}