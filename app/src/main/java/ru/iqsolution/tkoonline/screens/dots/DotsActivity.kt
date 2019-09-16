package ru.iqsolution.tkoonline.screens.dots

import android.os.Bundle
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity

class DotsActivity : BaseActivity(), DotsContract.ContractView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dots)
    }

    override fun onArtilesReady() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}