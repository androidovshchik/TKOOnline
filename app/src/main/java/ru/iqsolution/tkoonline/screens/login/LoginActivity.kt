@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import coil.api.load
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.common.wait.WaitDialog
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.services.AdminManager
import ru.iqsolution.tkoonline.services.workers.DeleteWorker
import ru.iqsolution.tkoonline.services.workers.MidnightWorker
import ru.iqsolution.tkoonline.services.workers.UpdateWorker

class LoginActivity : BaseActivity<LoginContract.Presenter>(), LoginContract.View {

    override val presenter: LoginPresenter by instance()

    private val adminManager: AdminManager by instance()

    private val fileManager: FileManager by instance()

    private val waitDialog: WaitDialog by instance()

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private lateinit var qrCode: QrCodeFragment

    private var alertDialog: AlertDialog? = null

    private var hasPrompted = false

    @SuppressLint("SetTextI18n")
    @Suppress("ConstantConditionIf")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        qrCode = fragmentManager.findFragmentById(R.id.barcode_fragment) as QrCodeFragment
        preferences.bulk {
            logout()
        }
        login_background.load(R.drawable.login_background)
        statusBarHeight.let {
            (login_layer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = it
            login_shadow.topPadding = it
        }
        login_menu.setOnClickListener {
            openDialog()
        }
        app_version.text = if (BuildConfig.PROD) {
            BuildConfig.VERSION_NAME
        } else {
            "v.${BuildConfig.VERSION_CODE}"
        }
        DeleteWorker.launch(applicationContext)
        MidnightWorker.launch(applicationContext)
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

    override fun onCanUpdate() {
        presenter.checkUpdates()
    }

    override fun onUpdateAvailable() {
        alertDialog = if (activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
            alert("Доступна новая версия приложения", "Обновление") {
                cancelButton {}
                positiveButton(if (adminManager.isDeviceOwner) "Установить" else "Скачать") {
                    waitDialog.show()
                    presenter.installUpdate(applicationContext)
                }
            }.show()
        } else {
            alert("Для установки требуется разблокировка", "Обновление") {
                cancelButton {}
                positiveButton("Продолжить") {
                    openPasswordDialog()
                }
            }.show()
        }
    }

    override fun cancelWork() {
        UpdateWorker.cancel(applicationContext)
    }

    override fun onUpdateEnd(success: Boolean) {
        waitDialog.dismiss()
        alertDialog = if (success) {
            if (adminManager.isDeviceOwner) {
                alert("Сейчас приложение будет закрыто", "Обновление") {
                    isCancelable = false
                }.show()
            } else {
                alert("Все готово для установки", "Обновление") {
                    cancelButton {}
                    positiveButton("Установить") {
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            type = "application/vnd.android.package-archive"
                            data = FileProvider.getUriForFile(
                                applicationContext,
                                "$packageName.fileprovider",
                                fileManager.apkFile
                            )
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        })
                    }
                }.show()
            }
        } else {
            alert("Не удалось скачать обновление", "Ошибка обновления") {
                cancelButton {}
                positiveButton("Повторить") {
                    waitDialog.show()
                    presenter.installUpdate(applicationContext)
                }
            }.show()
        }
    }

    override fun exportDb() {
        presenter.exportDb(applicationContext)
    }

    override fun onExportedDb(success: Boolean) {
        toast(if (success) "БД успешно экспортирована" else "Не удалось экспортировать БД")
    }

    override fun onUnhandledError(e: Throwable?) {
        qrCode.codeScanner.startPreview()
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
        super.onDestroy()
    }

    @SuppressLint("CommitTransaction")
    private inline fun transact(action: FragmentTransaction.() -> Unit) {
        fragmentManager.beginTransaction().apply(action)
    }
}