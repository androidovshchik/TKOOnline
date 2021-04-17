package ru.iqsolution.tkoonline.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import ru.iqsolution.tkoonline.extensions.getTopActivity
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.LockActivity

class UpdateReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        val preferences = Preferences(context)
        if (preferences.enableLock) {
            context.apply {
                if (activityManager.getTopActivity(packageName) == null) {
                    startActivity(intentFor<LockActivity>().newTask())
                }
            }
        }
    }
}