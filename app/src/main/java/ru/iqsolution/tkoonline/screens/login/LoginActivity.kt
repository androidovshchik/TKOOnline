package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity

class LoginActivity : BaseActivity(), LoginContract.ContractView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onArtilesReady() {

    }

}