package ru.iqsolution.tkoonline.screens

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.toast
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import ru.iqsolution.tkoonline.AdminManager
import ru.iqsolution.tkoonline.EXTRA_TROUBLE_EXIT
import ru.iqsolution.tkoonline.extensions.scanFiles
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity

class LockActivity : Activity(), DIAware {

    override val di by closestDI()

    private val preferences: Preferences by instance()

    private val fileManager: FileManager by instance()

    private val adminManager: AdminManager by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch(Dispatchers.Main) {
            fileManager.logsDir.listFiles()?.onEach {
                scanFiles(it.path)
                delay(200)
            }
        }
    }

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
            else -> finishAffinity()
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