package ru.iqsolution.tkoonline.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.iqsolution.tkoonline.WAIT_TIME
import ru.iqsolution.tkoonline.local.Preferences

class RebootReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val preferences = Preferences(context)
        preferences.blockTime = -WAIT_TIME
    }
}