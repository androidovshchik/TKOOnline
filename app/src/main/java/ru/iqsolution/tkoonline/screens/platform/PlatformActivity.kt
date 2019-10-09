package ru.iqsolution.tkoonline.screens.platform

import android.content.Intent
import android.os.Bundle
import androidx.collection.SimpleArrayMap
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

    private val photoErrors = SimpleArrayMap<Int, String>()

    private var confirmDialog: ConfirmDialog? = null

    private var hasPhotoChanges = false

    private var preFinishing = false

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform)
        presenter = PlatformPresenter().also {
            it.attachView(this)
        }
        platform = intent.getSerializableExtra(EXTRA_PLATFORM_PLATFORM) as PlatformContainers
        photoTypes.apply {
            addAll(intent.getSerializableExtra(EXTRA_PLATFORM_PHOTO_TYPES) as ArrayList<PhotoType>)
            forEach {
                if (it.isError == 1) {
                    photoErrors.put(it.id, it.shortName)
                }
            }
        }
        toolbar_back.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            if (!hasPhotoChanges) {
                closeDetails(false)
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
                EXTRA_PROBLEM_PLATFORM to platform,
                EXTRA_PROBLEM_PHOTO_TYPES to photoTypes
            )
        }
        platform_not_cleaned.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            preFinishing = true
            presenter.saveCleanEvents(platform.apply {
                reset()
                containers.forEach {
                    it.reset()
                }
            })
        }
        platform_cleaned.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            preFinishing = true
            presenter.saveCleanEvents(platform.apply {
                containers.forEach {
                    setFromEqual(it)
                }
            })
        }
        onCleanEvents(null)
        presenter.apply {
            loadCleanEvents(platform.kpId)
            loadPhotoEvents(platform.kpId)
        }
    }

    /**
     * Called once after create
     */
    override fun onCleanEvents(event: CleanEventRelated?) {
        event?.run {
            platform.containers.forEach {
                it.setFromEqual(clean)
                events.forEach { event ->
                    it.setFromEqual(event)
                }
            }
        }
        platform.apply {
            platform_regular.updateContainer(containerType, containers)
            platform_bunker.updateContainer(containerType, containers)
            platform_bulk.updateContainer(containerType, containers)
            platform_special.updateContainer(containerType, containers)
            platform_unknown.updateContainer(containerType, containers)
        }
    }

    override fun onPhotoEvents(events: List<PhotoEvent>) {
        gallery_before.updatePhotos(events)
        gallery_after.updatePhotos(events)
        platform.errors.clear()
        events.forEach {
            photoErrors.get(it.type)?.run {
                platform.addError(this)
            }
        }
        platform_map.setMarkers("[${presenter.toJson(platform)}]")
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

    override fun closeDetails(hasCleanChanges: Boolean) {
        preFinishing = true
        if (hasPhotoChanges || hasCleanChanges) {
            if (hasCleanChanges) {
                SendWorker.launch(applicationContext, platform.kpId)
            }
            setResult(RESULT_OK)
        } else {
            setResult(RESULT_CANCELED)
        }
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
                    hasPhotoChanges = true
                    presenter.loadPhotoEvents(platform.kpId)
                }
            }
        }
    }

    override fun onDestroy() {
        confirmDialog?.dismiss()
        platform_map.release()
        platform_regular.clear()
        platform_bunker.clear()
        platform_bulk.clear()
        platform_special.clear()
        platform_unknown.clear()
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_PHOTO = 400

        private const val REQUEST_PROBLEM = 410

        private const val URL = "file:///android_asset/platform.html"
    }
}