package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity
import ru.iqsolution.tkoonline.screens.containers.ContainersActivity

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity(), LoginContract.View {

    private lateinit var presenter: LoginPresenter

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(application).also {
            it.attachView(this)
        }
        presenter.clearAuthorization()
        login_menu.onClick {
            fragmentManager.beginTransaction().apply {
                fragmentManager.findFragmentByTag(settingsDialog.javaClass.simpleName)?.let {
                    remove(it)
                }
                fragmentManager.findFragmentByTag(passwordDialog.javaClass.simpleName)?.let {
                    remove(it)
                }
                addToBackStack(null)
                if (hasPrompted) {
                    settingsDialog.show(this, settingsDialog.javaClass.simpleName)
                } else {
                    passwordDialog.show(this, passwordDialog.javaClass.simpleName)
                }
            }
        }
    }

    override fun onRemovePrompt(success: Boolean) {
        hasPrompted = success
        fragmentManager.beginTransaction().apply {
            fragmentManager.findFragmentByTag(passwordDialog.javaClass.simpleName)?.let {
                remove(it)
            }
            if (success) {
                addToBackStack(null)
                settingsDialog.show(this, settingsDialog.javaClass.simpleName)
            } else {
                commit()
            }
        }
    }

    override fun onQrCode(value: String) {
        presenter.login(value)
    }

    override fun onAuthorized() {
        startActivity(intentFor<ContainersActivity>())
    }

    override fun onDestroy() {
        settingsDialog.dismiss()
        passwordDialog.dismiss()
        presenter.detachView()
        super.onDestroy()
    }
}