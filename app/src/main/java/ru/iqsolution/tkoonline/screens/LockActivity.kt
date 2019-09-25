package ru.iqsolution.tkoonline.screens

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.view.View
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

    val adminManager: AdminManager by instance()

    val preferences: Preferences by instance()

    private lateinit var content: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = findViewById<View>(android.R.id.content)
    }

    override fun onStart() {
        super.onStart()
        when (activityManager.lockTaskModeState) {
            ActivityManager.LOCK_TASK_MODE_NONE -> {
                if (preferences.enableLock) {
                    if (adminManager.setKioskMode(true)) {
                        content.post {
                            startLockTask()
                            startActivityNoop<LoginActivity>()
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
        startActivityNoop<LoginActivity>()
        finish()
    }

    override fun onBackPressed() {}
}