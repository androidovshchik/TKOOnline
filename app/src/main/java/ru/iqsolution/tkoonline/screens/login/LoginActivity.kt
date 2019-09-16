package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity

class LoginActivity : BaseActivity(), LoginContract.ContractView {

    private lateinit var presenter: LoginPresenter

    private lateinit var loginDialog: LoginDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(application)
        loginDialog = LoginDialog(this)
        login_menu.onClick {
            loginDialog.show()
        }
    }

    override fun onQrCode(value: String) {
        presenter.login(value)
    }

    override fun onAuthorized() {

    }

    override fun onDestroy() {
        loginDialog.dismiss()
        super.onDestroy()
    }
}