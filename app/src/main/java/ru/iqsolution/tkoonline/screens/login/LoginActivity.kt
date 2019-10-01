@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.sdk23.listeners.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.topPadding
import ru.iqsolution.tkoonline.DANGER_PERMISSIONS
import ru.iqsolution.tkoonline.GlideApp
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.screens.qrcode.ScannerListener

class LoginActivity : BaseActivity<LoginPresenter>(), LoginContract.View, ScannerListener, DialogCallback {

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(application).apply {
            attachView(this@LoginActivity)
            clearAuthorization()
        }
        GlideApp.with(applicationContext)
            .load(R.drawable.login_background)
            .into(login_background)
        statusBarHeight.let {
            (login_layer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = it
            login_shadow.topPadding = it
        }
        login_menu.onClick {
            openDialog(preferences)
        }
    }

    override fun onQrCode(value: String) {
        if (areGranted(*DANGER_PERMISSIONS)) {
            presenter.login(value)
        } else {
            toast("Требуется предоставить разрешения")
        }
    }

    override fun openDialog(preferences: Preferences) {
        transact {
            remove(passwordDialog)
            remove(settingsDialog)
            if (preferences.enableLock) {
                if (!hasPrompted) {
                    passwordDialog.show(this, passwordDialog.javaClass.simpleName)
                    return
                }
            }
            hasPrompted = true
            settingsDialog.show(this, settingsDialog.javaClass.simpleName)
        }
    }

    override fun openSettingsDialog() {
        hasPrompted = true
        transact {
            remove(passwordDialog)
            remove(settingsDialog)
            settingsDialog.show(this, settingsDialog.javaClass.simpleName)
        }
    }

    override fun openPasswordDialog() {
        transact {
            remove(passwordDialog)
            passwordDialog.show(this, passwordDialog.javaClass.simpleName)
        }
    }

    override fun enterKioskMode() {
        hasPrompted = false
        transact {
            remove(passwordDialog)
            commit()
        }
        settingsDialog.setLocked(true, null)
        startActivityNoop<LockActivity>()
    }

    override fun exitKioskMode() {
        startActivityNoop<LockActivity>()
    }

    override fun onAuthorized() {
        startActivityNoop<PlatformsActivity>()
        finish()
    }

    private val statusBarHeight: Int
        get() {
            val id = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (id > 0) {
                return resources.getDimensionPixelSize(id)
            }
            return 0
        }

    @SuppressLint("CommitTransaction")
    private inline fun transact(action: FragmentTransaction.() -> Unit) {
        fragmentManager.beginTransaction().apply(action)
    }

    override fun onBackPressed() {
        if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
            finishAffinity()
        }
    }
}