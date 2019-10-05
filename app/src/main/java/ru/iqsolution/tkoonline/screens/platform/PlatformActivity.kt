package ru.iqsolution.tkoonline.screens.platform

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity
import ru.iqsolution.tkoonline.screens.problem.ProblemActivity

class PlatformActivity : BaseActivity<PlatformPresenter>(), PlatformContract.View, GalleryListener {

    private lateinit var platform: PlatformContainers

    private val photoTypes = arrayListOf<PhotoType>()

    private var confirmDialog: ConfirmDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        presenter = PlatformPresenter(application).also {
            it.attachView(this)
            platform = it.fromJson(intent.getStringExtra(EXTRA_PLATFORM_PLATFORM), PlatformContainers::class.java)
            it.loadLastCleanEvent()
            it.loadPhotoEvents()
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
                REQUEST_PROBLEM,
                EXTRA_PROBLEM_PLATFORM to presenter.toJson(platform, platform.javaClass),
                EXTRA_PROBLEM_PHOTO_TYPES to photoTypes
            )
        }
        platform_not_cleaned.setOnClickListener {
            presenter.createCleanEvents()
        }
        platform_cleaned.setOnClickListener {
            presenter.createCleanEvents()
        }
    }

    override fun onPhotoClick(photoType: PhotoType.Default, photoEvent: PhotoEvent?) {
        val event = photoEvent ?: PhotoEvent(platform.kpId, photoType.id)
        startActivityNoop<PhotoActivity>(
            REQUEST_PHOTO,
            EXTRA_PHOTO_TITLE to photoType.description,
            EXTRA_PHOTO_EVENT to event,
            EXTRA_PHOTO_LINKED_IDS to platform.allLinkedIds
        )
    }

    override fun onLastCleanEvent(event: CleanEvent?) {

    }

    override fun onPhotoEvents(events: List<PhotoEvent>) {
        gallery_before.updatePhotos(events)
        gallery_after.updatePhotos(events)
    }

    override fun closeDetails(result: Int) {
        setResult(result)
        finish()
    }

    override fun onLocationResult(location: SimpleLocation) {
        platform_map.setLocation(location)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    presenter.loadPhotoEvents(platform.kpId)
                }
            }
        }
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        confirmDialog?.dismiss()
        platform_map.release()
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_PHOTO = 400

        private const val REQUEST_PROBLEM = 410

        private const val URL = "file:///android_asset/platform.html"
    }
}