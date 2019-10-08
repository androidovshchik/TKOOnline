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
            EXTRA_PLATFORM_PLATFORM to item,
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
        val location = preferences.location
        platformsAdapter.apply {
            primaryItems.notifyItems(primary, location)
            items.notifyItems(secondary, location)
            notifyDataSetChanged()
        }
        presenter.loadPhotoCleanEvents()
    }

    override fun onPhotoCleanEvents(photo: List<PhotoEvent>, clean: List<CleanEvent>) {
        platformsAdapter.apply {
            primaryItems.notifyItems(null, null, photo, null)
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

    override fun showError(e: Throwable?) {
        super.showError(e)
        platforms_refresh.isRefreshing = false
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

    private fun ArrayList<PlatformContainers>.notifyItems(
        platforms: List<PlatformContainers>? = null,
        location: SimpleLocation? = null,
        photo: List<PhotoEvent>? = null,
        clean: List<CleanEvent>? = null
    ) {
        platforms?.let {
            clear()
            addAll(it)
        }
        if (location != null) {
            forEach {
                it.setDistanceTo(location)
            }
        }
        val zone = DateTimeZone.forTimeZone(TimeZone.getDefault())
        if (photo != null) {
            forEach {
                for (event in photo) {
                    if (it.kpId == event.kpId) {
                        if (clean != null) {
                            // only for secondary
                            if (it.timestamp == 0L) {
                                it.timestamp = event.whenTime.withZone(zone).millis
                            }
                        }
                        photoErrors.get(event.type)?.run {
                            it.addError(this)
                        }
                    }
                }
            }
        }
        if (clean != null) {
            forEach {
                for (event in clean) {
                    if (it.kpId == event.kpId) {
                        val millis = event.whenTime.withZone(zone).millis
                        if (it.timestamp < millis) {
                            it.timestamp = millis
                        }
                        break
                    }
                }
            }
        }
    }

    companion object {

        private const val REQUEST_PLATFORM = 300

        private const val REQUEST_PHOTO = 310

        private const val URL = "file:///android_asset/platforms.html"
    }
}