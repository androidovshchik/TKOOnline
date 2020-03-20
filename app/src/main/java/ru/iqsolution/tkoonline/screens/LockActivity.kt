package ru.iqsolution.tkoonline.screens

import android.app.Activity
import android.app.ActivityManager
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.AdminManager
import ru.iqsolution.tkoonline.EXTRA_TROUBLE_EXIT
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity

class LockActivity : Activity(), KodeinAware {

    override val kodein by kodein()

    private val preferences: Preferences by instance()

    private val adminManager: AdminManager by instance()

    override fun onStart() {
        super.onStart()
        when (activityManager.lockTaskModeState) {
            ActivityManager.LOCK_TASK_MODE_NONE -> {
                if (preferences.enableLock) {
                    if (adminManager.setKioskMode(true)) {
                        // need startLockTask on resume
                        return
                    } else {
                        toast("Требуются права владельца устройства")
                    }
                }
                launchActivity()
                finish()
            }
            ActivityManager.LOCK_TASK_MODE_LOCKED -> {
                if (!preferences.enableLock) {
                    if (adminManager.setKioskMode(false)) {
                        stopLockTask()
                        launchActivity()
                        finish()
                        return
                    } else {
                        toast("Требуются права владельца устройства")
                    }
                }
                launchActivity()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFinishing) {
            if (activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask()
                launchActivity()
            }
        }
    }

    private fun launchActivity() {
        when {
            intent.getBooleanExtra(EXTRA_TROUBLE_EXIT, false) -> {
                preferences.invalidAuth = true
                startActivityNoop<LoginActivity>()
            }
            preferences.isLoggedIn -> startActivityNoop<PlatformsActivity>()
            else -> startActivityNoop<LoginActivity>()
        }
    }

    override fun onBackPressed() {}
}