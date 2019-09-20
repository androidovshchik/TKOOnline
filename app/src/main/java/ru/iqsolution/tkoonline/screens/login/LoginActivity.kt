package ru.iqsolution.tkoonline.screens.login

import android.app.ActivityManager
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivitySimply
import ru.iqsolution.tkoonline.screens.BaseActivity
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.containers.ContainersActivity

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity<LoginPresenter>(), LoginContract.View {

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(application).also {
            it.attachView(this)
            it.clearAuthorization()
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
        startActivitySimply<LockActivity>()
    }

    override fun onQrCode(value: String) {
        presenter.login(value)
    }

    override fun onAuthorized() {
        startActivitySimply<ContainersActivity>()
        finish()
    }

    override fun onBackPressed() {
        if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
            finishAffinity()
        }
    }
}