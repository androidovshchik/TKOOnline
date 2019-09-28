package ru.iqsolution.tkoonline.screens.platform

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.screens.base.BaseActivity

class PlatformActivity : BaseActivity<PlatformPresenter>(), PlatformContract.View {

    private var platform: Platform? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        presenter = PlatformPresenter(application).also {
            it.attachView(this)
            //platform = it.getContainer(intent.getIntExtra(EXTRA_ID, -1))
        }
        toolbar_back.onClick {
            finish()
        }
        platform_map.loadUrl("file:///android_asset/platform.html")
    }

    override fun onBackPressed() {}
}