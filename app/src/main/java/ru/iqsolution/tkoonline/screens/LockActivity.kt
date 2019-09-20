package ru.iqsolution.tkoonline.screens

import android.app.Activity
import android.app.ActivityManager
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.contentView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.services.AdminManager

class LockActivity : Activity(), KodeinAware {

    override val kodein by kodein()

    val adminManager: AdminManager by instance()

    val preferences: Preferences by instance()

    override fun onStart() {
        super.onStart()
        when (activityManager.lockTaskModeState) {
            ActivityManager.LOCK_TASK_MODE_NONE -> {
                if (preferences.enableLock) {
                    if (adminManager.setKioskMode(true)) {
                        contentView?.post {
                            startLockTask()
                            startLoginActivity()
                        }
                        return
                    } else {
                        toast("Требуются права владельца устройства")
                    }
                }
            }
            ActivityManager.LOCK_TASK_MODE_LOCKED -> {
                if (!preferences.enableLock) {
                    if (adminManager.setKioskMode(false)) {
                        stopLockTask()
                    } else {
                        toast("Требуются права владельца устройства")
                    }
                }
            }
        }
        startLoginActivity()
    }

    private fun startLoginActivity() {
        startActivity(intentFor<LoginActivity>())
        overridePendingTransition(0, 0)
    }

    override fun onBackPressed() {}
}