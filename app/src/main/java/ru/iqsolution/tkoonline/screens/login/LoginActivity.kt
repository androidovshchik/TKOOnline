@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import coil.load
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.toast
import org.jetbrains.anko.topPadding
import org.kodein.di.instance
import ru.iqsolution.tkoonline.AdminManager
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.base.AppAlertDialog
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.base.alert
import ru.iqsolution.tkoonline.screens.common.wait.WaitDialog
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.workers.DeleteWorker
import ru.iqsolution.tkoonline.workers.MidnightWorker
import ru.iqsolution.tkoonline.workers.UpdateWorker
import timber.log.Timber

class LoginActivity : BaseActivity<LoginContract.Presenter>(), LoginContract.View {

    override val presenter: LoginPresenter by instance()

    private val adminManager: AdminManager by instance()

    private val fileManager: FileManager by instance()

    private val waitDialog: WaitDialog by instance()

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private lateinit var qrCode: QrCodeFragment

    private var alertDialog: AppAlertDialog? = null

    private var hasPrompted = false

    private var skipCheckUpdates = false

    @SuppressLint("SetTextI18n")
    @Suppress("ConstantConditionIf")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        qrCode = fragmentManager.findFragmentById(R.id.barcode_fragment) as QrCodeFragment
        login_background.load(R.drawable.login_background)
        statusBarHeight.let {
            (login_layer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = it
            login_shadow.topPadding = it
        }
        login_menu.setOnClickListener {
            openDialog()
        }
        val version = if (BuildConfig.PROD) {
            "1.${BuildConfig.VERSION_CODE}"
        } else {
            "v.${BuildConfig.VERSION_CODE}"
        }
        Timber.i("App version: $version")
        app_version.text = version
        DeleteWorker.launch(applicationContext)
        MidnightWorker.launch(applicationContext)
        presenter.logout()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            if (!skipCheckUpdates) {
                presenter.checkUpdates()
            }
        }
    }

    /**
     * Here permissions are granted
     */
    @WorkerThread
    override fun onQrCode(value: String) {
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
        skipCheckUpdates = false
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

    override fun onUpdateAvailable() {
        skipCheckUpdates = true
        alertDialog = alert("Доступна новая версия приложения", "Обновление") {
            cancelButton()
            positiveButton(if (adminManager.isDeviceOwner) "Установить" else "Скачать") { _, _ ->
                waitDialog.show()
                presenter.installUpdate(applicationContext)
            }
        }.display()
    }

    override fun cancelWork() {
        UpdateWorker.cancel(applicationContext)
    }

    override fun onUpdateEnd(success: Boolean) {
        waitDialog.dismiss()
        alertDialog = if (success) {
            if (adminManager.isDeviceOwner) {
                alert("Сейчас приложение будет закрыто", "Обновление") {
                    setCancelable(false)
                }.display()
            } else {
                alert("Все готово для установки", "Обновление") {
                    cancelButton()
                    positiveButton("Установить") { _, _ ->
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            type = "application/vnd.android.package-archive"
                            data = FileProvider.getUriForFile(
                                applicationContext,
                                "$packageName.fileprovider",
                                fileManager.apkFile ?: return@positiveButton
                            )
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        })
                    }
                }.display()
            }
        } else {
            alert("Не удалось скачать обновление", "Ошибка обновления") {
                cancelButton()
                positiveButton("Повторить") { _, _ ->
                    waitDialog.show()
                    presenter.installUpdate(applicationContext)
                }
            }.display()
        }
    }

    override fun exportDb() {
        presenter.exportDb()
    }

    override fun onExportedDb(success: Boolean) {
        toast(if (success) "БД успешно экспортирована" else "Не удалось экспортировать БД")
    }

    override fun onUnhandledError(e: Throwable?) {
        qrCode.startScan()
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
        if (activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
            finishAffinity()
        }
    }

    override fun onDestroy() {
        alertDialog?.dismiss()
        waitDialog.dismiss()
        cancelWork()
        super.onDestroy()
    }

    @SuppressLint("CommitTransaction")
    private inline fun transact(action: FragmentTransaction.() -> Unit) {
        fragmentManager.beginTransaction().apply(action)
    }
}