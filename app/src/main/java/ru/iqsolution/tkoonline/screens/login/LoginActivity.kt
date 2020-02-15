@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.FragmentTransaction
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import coil.api.load
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.powerManager
import org.jetbrains.anko.toast
import org.jetbrains.anko.topPadding
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.screens.qrcode.ScannerListener
import ru.iqsolution.tkoonline.services.TelemetryService
import ru.iqsolution.tkoonline.services.workers.DeleteWorker

class LoginActivity : BaseActivity<LoginPresenter>(), LoginContract.View, ScannerListener, SettingsListener {

    override val presenter: LoginPresenter by instance()

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        preferences.bulk {
            logout()
        }
        DeleteWorker.launch(applicationContext)
        login_background.load(R.drawable.login_background)
        statusBarHeight.let {
            (login_layer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = it
            login_shadow.topPadding = it
        }
        login_menu.setOnClickListener {
            openDialog()
        }
        app_version.text = "v.${BuildConfig.VERSION_CODE}"
        // NOTICE this violates Google Play policy
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            startActivity(
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }

    /**
     * Here permissions are granted
     */
    override fun onQrCode(value: String) {
        if (activityManager.isRunning<TelemetryService>()) {
            return
        }
        presenter.login(value)
    }

    override fun openDialog() {
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

    override fun exportDb() {
        presenter.export(applicationContext)
    }

    override fun enterKioskMode() {
        hasPrompted = false
        preferences.enableLock = true
        settingsDialog.setAsLocked(true)
        transact {
            remove(passwordDialog)
            commit()
        }
        startActivityNoop<LockActivity>()
    }

    override fun exitKioskMode() {
        preferences.enableLock = false
        settingsDialog.setAsLocked(false)
        startActivityNoop<LockActivity>()
    }

    override fun onLoggedIn() {
        startActivityNoop<PlatformsActivity>()
        finish()
    }

    override fun onExported(success: Boolean) {
        toast(if (success) "БД успешно экспортирована" else "Не удалось экспортировать БД")
    }

    override fun showError(e: Throwable?) {
        super.showError(e)
        presenter.reset()
    }

    private val statusBarHeight: Int
        get() {
            val id = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (id > 0) {
                return resources.getDimensionPixelSize(id)
            }
            return 0
        }

    override fun onBackPressed() {
        if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
            finishAffinity()
        }
    }

    @SuppressLint("CommitTransaction")
    private inline fun transact(action: FragmentTransaction.() -> Unit) {
        fragmentManager.beginTransaction().apply(action)
    }
}