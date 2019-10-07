package ru.iqsolution.tkoonline.screens.platform

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_platform.*
import kotlinx.android.synthetic.main.include_platform.*
import kotlinx.android.synthetic.main.include_toolbar.*
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.setTextBoldSpan
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.CleanEventRelated
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity
import ru.iqsolution.tkoonline.screens.problem.ProblemActivity
import ru.iqsolution.tkoonline.services.workers.SendWorker

/**
 * Returns [android.app.Activity.RESULT_OK] if there were changes
 */
class PlatformActivity : BaseActivity<PlatformPresenter>(), PlatformContract.View, ConfirmListener, GalleryListener {

    private lateinit var platform: PlatformContainers

    private val photoTypes = arrayListOf<PhotoType>()

    private var confirmDialog: ConfirmDialog? = null

    private var preFinishing = false

    private var mHasChanges = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        presenter = PlatformPresenter().also {
            it.attachView(this)
            platform = it.fromJson(intent.getStringExtra(EXTRA_PLATFORM_PLATFORM), PlatformContainers::class.java)
        }
        toolbar_back.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            if (confirmDialog == null) {
                confirmDialog = ConfirmDialog(this)
            }
            confirmDialog?.let {
                if (!it.isShowing) {
                    it.show()
                }
            }
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
        platform_report.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            startActivityNoop<ProblemActivity>(
                REQUEST_PROBLEM,
                EXTRA_PROBLEM_PLATFORM to presenter.toJson(platform, platform.javaClass),
                EXTRA_PROBLEM_PHOTO_TYPES to photoTypes
            )
        }
        platform_not_cleaned.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            preFinishing = true
            presenter.saveCleanEvents(platform)
        }
        platform_cleaned.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            preFinishing = true
            presenter.saveCleanEvents(platform)
        }
        presenter.apply {
            loadCleanEvents(platform.kpId)
            loadPhotoEvents(platform.kpId)
        }
    }

    override fun onCleanEvents(event: CleanEventRelated?) {
        platform_regular.setFrom(event)
        platform_bunker.setFrom(event)
        platform_bulk.setFrom(event)
        platform_special.setFrom(event)
        platform_unknown.setFrom(event)
    }

    override fun onPhotoEvents(events: List<PhotoEvent>) {
        gallery_before.updatePhotos(events)
        gallery_after.updatePhotos(events)
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

    override fun closeDetails(hasChanges: Boolean) {
        if (mHasChanges || hasChanges) {

        } else {

        }
        SendWorker.launch(applicationContext, platform.kpId)
        setResult(if (mHasChanges || hasChanges) RESULT_OK else RESULT_CANCELED)
        finish()
    }

    override fun onLocationResult(location: SimpleLocation) {
        platform_map.setLocation(location)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PHOTO, REQUEST_PROBLEM -> {
                if (resultCode == RESULT_OK) {
                    mHasChanges = true
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

    private fun ContainerLayout.setFrom(event: CleanEventRelated?) {
        event?.run {
            platform.containers.forEach {
                it.setFromSame(clean)
                events.forEach { event ->
                    it.setFromSame(event)
                }
            }
        } ?: run {
            platform_regular.container = platform.containers.getOrNull(0)
            platform_bunker.container = platform.containers.getOrNull(1)
            platform_bulk.container = platform.containers.getOrNull(2)
            platform_special.container = platform.containers.getOrNull(3)
            platform_unknown.container = platform.containers.getOrNull(4)
        }
    }

    companion object {

        private const val REQUEST_PHOTO = 400

        private const val REQUEST_PROBLEM = 410

        private const val URL = "file:///android_asset/platform.html"
    }
}