package ru.iqsolution.tkoonline.screens.platform

import android.location.Location
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_PLATFORM
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BaseActivity

class PlatformActivity : BaseActivity<PlatformPresenter>(), PlatformContract.View {

    private lateinit var platform: PlatformContainers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        presenter = PlatformPresenter(application).apply {
            attachView(this@PlatformActivity)
            platform = platformFromJson(intent.getStringExtra(EXTRA_PLATFORM))
        }
        toolbar_back.onClick {
            finish()
        }
        platform_map.apply {
            loadUrl(URL)
            preferences.apply {
                lastTime?.let {
                    setLocation(lastLat.toDouble(), lastLon.toDouble())
                }
            }
            moveTo(platform.latitude, platform.longitude)
        }
        platform_not_cleaned.onClick {
            finish()
        }
        platform_cleaned.onClick {
            finish()
        }
    }

    override fun onLocationResult(location: Location) {
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