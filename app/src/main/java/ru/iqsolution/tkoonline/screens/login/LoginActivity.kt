package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity
import ru.iqsolution.tkoonline.screens.containers.ContainersActivity

class LoginActivity : BaseActivity(), LoginContract.View {

    private lateinit var presenter: LoginPresenter

    private lateinit var loginDialog: LoginDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(application).also {
            it.attachView(this)
        }
        loginDialog = LoginDialog(this)
        presenter.clearAuthorization()
        login_menu.onClick {
            loginDialog.show()
        }
    }

    override fun onQrCode(value: String) {
        presenter.login(value)
    }

    override fun onAuthorized() {
        startActivity(intentFor<ContainersActivity>())
    }

    override fun onDestroy() {
        loginDialog.dismiss()
        presenter.detachView()
        super.onDestroy()
    }
}