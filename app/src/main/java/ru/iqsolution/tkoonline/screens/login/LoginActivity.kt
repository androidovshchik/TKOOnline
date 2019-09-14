package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity

class LoginActivity : BaseActivity(), LoginContract.ContractView {

    private lateinit var loginDialog: LoginDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginDialog = LoginDialog(this)
        login_menu.onClick {
            loginDialog.show()
        }
    }

    override fun onArtilesReady() {

    }

    override fun onDestroy() {
        loginDialog.dismiss()
        super.onDestroy()
    }
}