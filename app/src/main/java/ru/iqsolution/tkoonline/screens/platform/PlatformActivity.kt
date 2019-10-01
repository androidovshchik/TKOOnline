package ru.iqsolution.tkoonline.screens.platform

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.platform_info.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_PLATFORM
import ru.iqsolution.tkoonline.FORMAT_TIME
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
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
        toolbar_title.text = platform.address
        platform_map.apply {
            loadUrl(URL)
            setLocation(preferences.location)
            moveTo(platform.latitude, platform.longitude)
        }
        platform_id.text = platform.kpId.toString()
        platform_range.text = getString(
            R.string.container_range,
            platform.timeLimitFrom.toString(FORMAT_TIME),
            platform.timeLimitTo.toString(FORMAT_TIME)
        )
        platform_divider.visibility = View.VISIBLE
        platform_not_cleaned.onClick {
            finish()
        }
        platform_cleaned.onClick {
            finish()
        }
    }

    override fun onLocationResult(location: SimpleLocation) {
        platform_map.setLocation(location)
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