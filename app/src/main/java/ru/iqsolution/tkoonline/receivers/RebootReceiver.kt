package ru.iqsolution.tkoonline.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import ru.iqsolution.tkoonline.WAIT_TIME
import ru.iqsolution.tkoonline.extensions.getActivities
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.LockActivity

class RebootReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val preferences = Preferences(context)
        preferences.blockTime = -WAIT_TIME
        if (preferences.enableLock) {
            context.apply {
                if (activityManager.getActivities(packageName) <= 0) {
                    startActivity(
                        intentFor<LockActivity>().clearTop()
                            .newTask()
                    )
                }
            }
        }
    }
}