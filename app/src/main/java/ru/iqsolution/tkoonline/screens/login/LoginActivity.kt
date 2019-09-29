package ru.iqsolution.tkoonline.screens.login

import android.app.ActivityManager
import android.os.Bundle
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.sdk23.listeners.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.topPadding
import ru.iqsolution.tkoonline.DANGER_PERMISSIONS
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.screens.qrcode.ScannerListener

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity<LoginPresenter>(), LoginContract.View, ScannerListener {

    private lateinit var dialogManager: DialogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(application).also {
            it.attachView(this)
            it.clearAuthorization()
        }
        dialogManager = DialogManager(this)
        statusBarHeight.let {
            (login_layer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = it
            login_shadow.topPadding = it
        }
        login_menu.onClick {
            dialogManager.open(preferences)
        }
    }

    override fun onQrCode(value: String) {
        if (areGranted(*DANGER_PERMISSIONS)) {
            presenter.login(value)
        } else {
            toast("Требуется предоставить разрешения")
        }
    }

    override fun onPrompted() {
        dialogManager.onPrompted()
    }

    override fun onKioskMode(enter: Boolean) {
        dialogManager.onKioskMode(enter)
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

    override fun onBackPressed() {
        if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
            finishAffinity()
        }
    }
}