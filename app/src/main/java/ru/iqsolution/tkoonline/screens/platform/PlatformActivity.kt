package ru.iqsolution.tkoonline.screens.platform

import android.location.Location
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_PLATFORM
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.models.Platform
import ru.iqsolution.tkoonline.screens.base.BaseActivity

class PlatformActivity : BaseActivity<PlatformPresenter>(), PlatformContract.View {

    private lateinit var platform: Platform

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        presenter = PlatformPresenter(application).also {
            it.attachView(this)
            platform = it.parsePlatform(intent.getStringExtra(EXTRA_PLATFORM))
        }
        toolbar_back.onClick {
            finish()
        }
        platform_map.loadUrl("file:///android_asset/platform.html")
    }

    override fun onLocationResult(location: Location) {
        platform_map.setLocation(location.latitude, location.longitude)
    }

    override fun onBackPressed() {}
}