package ru.iqsolution.tkoonline.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class AdminReceiver : DeviceAdminReceiver() {

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        Timber.d("onLockTaskModeEntering")
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        Timber.d("onLockTaskModeExiting")
    }
}
