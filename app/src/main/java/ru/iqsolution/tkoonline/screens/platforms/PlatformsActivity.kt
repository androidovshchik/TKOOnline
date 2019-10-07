package ru.iqsolution.tkoonline.screens.platforms

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.collection.SimpleArrayMap
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_platforms.*
import org.joda.time.DateTimeZone
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity
import ru.iqsolution.tkoonline.screens.platform.PlatformActivity
import ru.iqsolution.tkoonline.services.workers.SendWorker
import java.util.*

class PlatformsActivity : BaseActivity<PlatformsPresenter>(), PlatformsContract.View, WaitListener,
    AdapterListener<PlatformContainers> {

    override val attachService = true

    private lateinit var platformsAdapter: PlatformsAdapter

    private val photoTypes = arrayListOf<PhotoType>()

    private val photoErrors = SimpleArrayMap<Int, String>()

    private var waitDialog: WaitDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platforms)
        presenter = PlatformsPresenter().also {
            it.attachView(this)
        }
        platformsAdapter = PlatformsAdapter(applicationContext).also {
            it.setListener(this)
        }
        platforms_map.apply {
            loadUrl(URL)
            setLocation(preferences.location)
        }
        platforms_refresh.setOnRefreshListener {
            presenter.loadPlatformsTypes(true)
        }
        platforms_list.apply {
            addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL).apply {
                ContextCompat.getDrawable(applicationContext, R.drawable.divider)?.let {
                    setDrawable(it)
                }
            })
            adapter = platformsAdapter
        }
        platforms_complete.setOnClickListener {
            showLoading()
            telemetryService?.stopTelemetry()
            presenter.logout(applicationContext)
        }
        if (preferences.allowPhotoRefKp) {
            platforms_placeholder.visibility = View.VISIBLE
            platforms_photo.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    val photoType = PhotoType.Default.OTHER
                    startActivityNoop<PhotoActivity>(
                        REQUEST_PHOTO,
                        EXTRA_PHOTO_TITLE to photoType.description,
                        EXTRA_PHOTO_EVENT to PhotoEvent(photoType.id)
                    )
                }
            }
        }
        presenter.loadPlatformsTypes(false)
    }

    override fun onAdapterEvent(position: Int, item: PlatformContainers, param: Any?) {
        startActivityNoop<PlatformActivity>(
            REQUEST_PLATFORM,
            EXTRA_PLATFORM_PLATFORM to presenter.toJson(item, item.javaClass),
            EXTRA_PLATFORM_PHOTO_TYPES to photoTypes
        )
    }

    override fun onReceivedTypes(types: List<PhotoType>) {
        photoTypes.apply {
            clear()
            addAll(types)
        }
        photoErrors.clear()
        types.forEach {
            if (it.isError == 1) {
                photoErrors.put(it.id, it.shortName)
            }
        }
    }

    override fun changeMapPosition(latitude: Double, longitude: Double) {
        platforms_map.moveTo(latitude, longitude)
    }

    override fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>) {
        platformsAdapter.apply {
            primaryItems.notifyItems(primary)
            items.notifyItems(secondary)
            notifyDataSetChanged()
        }
        presenter.loadPhotoCleanEvents()
    }

    override fun onPhotoCleanEvents(photo: List<PhotoEvent>, clean: List<CleanEvent>) {
        platformsAdapter.apply {
            primaryItems.notifyItems(null, null, photo, clean)
            items.apply {
                notifyItems(null, null, photo, clean)
                sortByDescending { it.timestamp }
            }
            notifyDataSetChanged()
            // primary platforms will overlay secondary in such order
            platforms_map.setMarkers(presenter.toJson(items), presenter.toJson(primaryItems))
        }
        platforms_refresh.isRefreshing = false
    }

    override fun onLocationResult(location: SimpleLocation) {
        platforms_map.setLocation(location)
        platformsAdapter.apply {
            primaryItems.apply {
                notifyItems(null, location)
                sortBy { it.meters }
            }
            items.notifyItems(null, location)
            notifyDataSetChanged()
        }
    }

    override fun cancelWork() {
        SendWorker.cancel(applicationContext)
        telemetryService?.startTelemetry()
    }

    override fun onLoggedOut() {
        startActivityNoop<LoginActivity>()
        finish()
    }

    private fun showLoading() {
        if (waitDialog == null) {
            waitDialog = WaitDialog(this)
        }
        waitDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PLATFORM -> {
                if (resultCode == RESULT_OK) {
                    presenter.loadPhotoCleanEvents()
                }
            }
            REQUEST_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    SendWorker.launch(applicationContext, -1, true)
                }
            }
        }
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        waitDialog?.dismiss()
        platforms_map.release()
        super.onDestroy()
    }

    private fun List<PlatformContainers>.notifyItems(
        platforms: List<PlatformContainers>? = null,
        location: SimpleLocation? = null,
        photo: List<PhotoEvent>? = null,
        clean: List<CleanEvent>? = null
    ) {
        platforms?.let {
            items.apply {
                clear()
                addAll(it)
            }
        }
        if (location != null) {
            items.forEach {
                it.setDistanceTo(location)
            }
        }
        val timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
        for (platform in photo) {
            if (it.kpId == platform.kpId) {
                if (platform.timestamp == 0L) {
                    platform.timestamp = it.whenTime.withZone(timeZone).millis
                }
                photoErrors.get(it.type)?.run {
                    platform.addError(this)
                }
            }
        }
        for (platform in clean) {
            if (it.kpId == platform.kpId) {
                val millis = it.whenTime.withZone(timeZone).millis
                if (platform.timestamp < millis) {
                    platform.timestamp = millis
                }
                break
            }
        }
    }

    companion object {

        private const val REQUEST_PLATFORM = 300

        private const val REQUEST_PHOTO = 310

        private const val URL = "file:///android_asset/platforms.html"
    }
}