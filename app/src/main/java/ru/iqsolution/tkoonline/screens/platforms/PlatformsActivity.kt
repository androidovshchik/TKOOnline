package ru.iqsolution.tkoonline.screens.platforms

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_platforms.*
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

class PlatformsActivity : BaseActivity<PlatformsPresenter>(), PlatformsContract.View, WaitListener,
    AdapterListener<PlatformContainers> {

    override val attachService = true

    private lateinit var platformsAdapter: PlatformsAdapter

    private val photoTypes = arrayListOf<PhotoType>()

    private var waitDialog: WaitDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platforms)
        platformsAdapter = PlatformsAdapter(applicationContext).apply {
            setListener(this@PlatformsActivity)
        }
        presenter = PlatformsPresenter().apply {
            attachView(this@PlatformsActivity)
            loadPlatformsTypes(false)
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
                        null,
                        EXTRA_PHOTO_TITLE to photoType.description,
                        EXTRA_PHOTO_EVENT to PhotoEvent(photoType.id)
                    )
                }
            }
        }
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
    }

    override fun changeMapPosition(latitude: Double, longitude: Double) {
        platforms_map.moveTo(latitude, longitude)
    }

    override fun onReceivedPrimary(platforms: List<PlatformContainers>) {
        platformsAdapter.apply {
            notifyPrimaryItems(preferences.location, platforms)
            notifyDataSetChanged()
        }
        platforms_refresh.isRefreshing = false
    }

    override fun onReceivedSecondary(platforms: List<PlatformContainers>) {
        platformsAdapter.apply {
            notifySecondaryItems(preferences.location, platforms)
            notifyDataSetChanged()
        }
    }

    /**
     * NOTICE primary platforms will overlay secondary in such order
     */
    override fun updateMapMarkers(primary: String, secondary: String) {
        platforms_map.setMarkers(secondary, primary)
    }

    override fun onLocationResult(location: SimpleLocation) {
        platforms_map.setLocation(location)
        platformsAdapter.apply {
            notifyPrimaryItems(location)
            notifySecondaryItems(location)
            notifyDataSetChanged()
        }
    }

    override fun onPhotoEvents(events: List<PhotoEvent>) {
        val errorNames = SimpleArrayMap<Int, String>()
        val timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
        responseTypes.data.forEach {
            if (it.isError == 1) {
                errorNames.put(it.id, it.shortName)
            }
        }
        secondary.apply {
            sortByDescending { it.timestamp }
        }
        for (platform in secondary) {
            if (it.kpId == platform.kpId) {
                if (platform.timestamp == 0L) {
                    platform.timestamp = it.whenTime.withZone(timeZone).millis
                }
                errorNames.get(it.type)?.run {
                    platform.addError(this)
                }
            }
        }
    }

    override fun onCleanEvents(events: List<CleanEvent>) {
        for (platform in secondary) {
            if (it.kpId == platform.kpId) {
                val millis = it.whenTime.withZone(timeZone).millis
                if (platform.timestamp < millis) {
                    platform.timestamp = millis
                }
                break
            }
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

    private fun PlatformsAdapter.notifyPrimaryItems(
        location: SimpleLocation?,
        platforms: List<PlatformContainers>? = null
    ) {
        platforms?.let {
            primaryItems.apply {
                clear()
                addAll(it)
            }
        }
        if (location != null) {
            primaryItems.forEach {
                it.meters = it.getDistance(location)
            }
            primaryItems.sortBy { it.meters }
        }
    }

    private fun PlatformsAdapter.notifySecondaryItems(
        location: SimpleLocation?,
        platforms: List<PlatformContainers>? = null
    ) {
        platforms?.let {
            items.apply {
                clear()
                addAll(it)
            }
        }
        if (location != null) {
            items.forEach {
                it.meters = it.getDistance(location)
            }
        }
    }

    companion object {

        private const val REQUEST_PLATFORM = 300

        private const val REQUEST_PHOTO = 310

        private const val URL = "file:///android_asset/platforms.html"
    }
}