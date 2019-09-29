package ru.iqsolution.tkoonline.screens.platform

import android.location.Location
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_PLATFORM
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.models.Platform
import ru.iqsolution.tkoonline.screens.MapView
import ru.iqsolution.tkoonline.screens.base.BaseActivity

class PlatformActivity : BaseActivity<PlatformPresenter>(), PlatformContract.View, MapView.Listener {

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
        platform_map.loadUrl(URL)
        platform_not_cleaned.onClick {
            finish()
        }
        platform_cleaned.onClick {
            finish()
        }
    }

    override fun onPageFinished(url: String) {
        if (url == URL) {
            platform_map.apply {
                setLocation(preferences.latitude.toDouble(), preferences.longitude.toDouble())
                moveTo(platform.latitude, platform.longitude)
            }
        }
    }

    override fun onLocationResult(location: Location) {
        super.onLocationResult(location)
        platform_map.setLocation(location.latitude, location.longitude)
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        platform_map.release()
        super.onDestroy()
    }

    companion object {

        private const val URL = "file:///android_asset/platform.html"
    }
}