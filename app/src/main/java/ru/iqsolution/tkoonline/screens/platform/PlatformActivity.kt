package ru.iqsolution.tkoonline.screens.platform

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import ru.iqsolution.tkoonline.EXTRA_PLATFORM_PLATFORM
import ru.iqsolution.tkoonline.FORMAT_TIME
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity
import ru.iqsolution.tkoonline.screens.problem.ProblemActivity

class PlatformActivity : BaseActivity<PlatformPresenter>(), PlatformContract.View, GalleryListener {

    private lateinit var platform: PlatformContainers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        presenter = PlatformPresenter(application).apply {
            attachView(this@PlatformActivity)
            platform = fromJson(intent.getStringExtra(EXTRA_PLATFORM_PLATFORM), PlatformContainers::class.java)
        }
        toolbar_back.setOnClickListener {
            finish()
        }
        toolbar_title.text = platform.address
        platform_map.apply {
            loadUrl(URL)
            setLocation(preferences.location)
            moveTo(platform.latitude, platform.longitude)
        }
        platform_id.setTextBoldSpan(getString(R.string.platform_id, platform.kpId), 0, 3)
        platform_range.setTextBoldSpan(
            getString(
            R.string.platform_range,
            platform.timeLimitFrom.toString(FORMAT_TIME),
            platform.timeLimitTo.toString(FORMAT_TIME)
            ), 2, 7, 11, 16
        )
        if (platform.isEmpty) {
            platform_divider.visibility = View.VISIBLE
        }
        platform_report.setOnClickListener {
            startActivityNoop<ProblemActivity>(
                null,
                EXTRA_PHOTO_TYPES to 0
            )
        }
        platform_not_cleaned.setOnClickListener {
            finish()
        }
        platform_cleaned.setOnClickListener {
            finish()
        }
    }

    override fun onPhotoClick(photoEvent: PhotoEvent?) {
        startActivityNoop<PhotoActivity>(
            REQUEST_PHOTO,
            EXTRA_PHOTO_TITLE to photoType.description,
            EXTRA_PHOTO_KP_ID to platform.kpId,
            EXTRA_PHOTO_TYPE to photoType.id
        )
    }

    override fun onPhotoEvents(events: List<PhotoEvent>) {

    }

    override fun onCleanEvents(events: List<CleanEvent>) {

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

        private const val REQUEST_PHOTO = 400

        private const val URL = "file:///android_asset/platform.html"
    }
}