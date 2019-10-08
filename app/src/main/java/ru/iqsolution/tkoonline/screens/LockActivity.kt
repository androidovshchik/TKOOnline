package ru.iqsolution.tkoonline.screens

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.services.AdminManager

class LockActivity : Activity(), KodeinAware {

    override val kodein by kodein()

    val preferences: Preferences by instance()

    private lateinit var adminManager: AdminManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminManager = AdminManager(applicationContext)
    }

    override fun onStart() {
        super.onStart()
        when (activityManager.lockTaskModeState) {
            ActivityManager.LOCK_TASK_MODE_NONE -> {
                if (preferences.enableLock) {
                    if (adminManager.setKioskMode(true)) {
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
        startActivityNoop<LoginActivity>()
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (!isFinishing) {
            startLockTask()
            startActivityNoop<LoginActivity>()
        }
    }

    override fun onBackPressed() {}
}