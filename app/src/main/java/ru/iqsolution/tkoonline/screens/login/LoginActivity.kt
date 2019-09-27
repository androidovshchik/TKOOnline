package ru.iqsolution.tkoonline.screens.login

import android.app.ActivityManager
import android.os.Bundle
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.sdk23.listeners.onClick
import org.jetbrains.anko.topPadding
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity<LoginPresenter>(), LoginContract.View {

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    private val statusBarHeight: Int
        get() {
            val id = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (id > 0) {
                return resources.getDimensionPixelSize(id)
            }
            return 0
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(application).also {
            it.attachView(this)
            it.clearAuthorization()
        }
        statusBarHeight.let {
            (login_layer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = it
            login_shadow.topPadding = it
        }
        login_menu.onClick {
            fragmentManager.beginTransaction().apply {
                fragmentManager.findFragmentByTag(settingsDialog.javaClass.simpleName)?.let {
                    remove(it)
                }
                fragmentManager.findFragmentByTag(passwordDialog.javaClass.simpleName)?.let {
                    remove(it)
                }
                if (hasPrompted) {
                    settingsDialog.show(this, settingsDialog.javaClass.simpleName)
                } else {
                    passwordDialog.show(this, passwordDialog.javaClass.simpleName)
                }
            }
        }
    }

    override fun onSuccessPrompt() {
        hasPrompted = true
        fragmentManager.beginTransaction().apply {
            fragmentManager.findFragmentByTag(passwordDialog.javaClass.simpleName)?.let {
                remove(it)
            }
            settingsDialog.show(this, settingsDialog.javaClass.simpleName)
        }
    }

    override fun onKioskMode(enter: Boolean) {
        if (enter) {
            hasPrompted = false
        }
        startActivityNoop<LockActivity>()
    }

    override fun onQrCode(value: String) {
        presenter.login(value)
    }

    override fun onAuthorized() {
        startActivityNoop<PlatformsActivity>()
        finish()
    }

    override fun onBackPressed() {
        if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
            finishAffinity()
        }
    }
}