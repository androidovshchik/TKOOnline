package ru.iqsolution.tkoonline.screens.photo

import android.os.Bundle
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity

class PhotoActivity : BaseActivity<PhotoPresenter>(), PhotoContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        presenter = PhotoPresenter(application).also {
            it.attachView(this)
        }
        toolbar_back.onClick {
            finish()
        }
    }

    override fun onBackPressed() {}
}