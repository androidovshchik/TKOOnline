package ru.iqsolution.tkoonline.screens.dots

import android.os.Bundle
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity
import ru.iqsolution.tkoonline.screens.login.LoginContract

class DotsActivity : BaseActivity(), LoginContract.ContractView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dots)
    }

    override fun onArtilesReady() {

    }

}