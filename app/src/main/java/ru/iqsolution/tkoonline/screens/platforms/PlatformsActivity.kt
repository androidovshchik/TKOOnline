package ru.iqsolution.tkoonline.screens.platforms

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.collection.SimpleArrayMap
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationSettingsStates
import kotlinx.android.synthetic.main.activity_platforms.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.PlatformStatus
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

    private lateinit var platformsAdapter: PlatformsAdapter

    private val photoTypes = arrayListOf<PhotoType>()

    private val photoErrors = SimpleArrayMap<Int, String>()

    private var waitDialog: WaitDialog? = null

    private var refreshTime: DateTime? = null

    private var platformClicked = false

    private var locationCount = 0L

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
            if (platformClicked) {
                return@setOnClickListener
            }
            showLoading()
            presenter.logout(applicationContext)
        }
        if (preferences.allowPhotoRefKp) {
            platforms_placeholder.visibility = View.VISIBLE
            platforms_photo.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    if (platformClicked) {
                        return@setOnClickListener
                    }
                    val photoType = PhotoType.Default.OTHER
                    startActivityNoop<PhotoActivity>(
                        REQUEST_PHOTO,
                        EXTRA_PHOTO_TITLE to photoType.description,
                        EXTRA_PHOTO_EVENT to PhotoEvent(photoType.id).apply {
                            ready = true
                        }
                    )
                }
            }
        }
        presenter.loadPlatformsTypes(false)
    }

    override fun onStart() {
        super.onStart()
        platformClicked = false
    }

    override fun onAdapterEvent(position: Int, item: PlatformContainers, param: Any?) {
        if (platformClicked) {
            return
        }
        platformClicked = true
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
        refreshTime = DateTime.now()
        platformsAdapter.apply {
            primaryItems.notifyItems(true, primary)
            items.notifyItems(false, secondary)
            notifyDataSetChanged()
        }
        presenter.apply {
            loadPhotoCleanEvents()
            if (preferences.showRoute) {
                loadRoute()
            }
        }
    }

    override fun onPhotoCleanEvents(photoEvents: List<PhotoEvent>, cleanEvents: List<CleanEvent>) {
        val location = preferences.location
        platformsAdapter.apply {
            primaryItems.apply {
                notifyItems(true, null, location, photoEvents, cleanEvents)
                if (location != null) {
                    sortBy { it.meters }
                }
            }
            items.apply {
                notifyItems(false, null, location, photoEvents, cleanEvents)
                sortByDescending { it.timestamp }
            }
            notifyDataSetChanged()
            // primary platforms will overlay secondary in such order
            platforms_map.setMarkers(presenter.toJson(items), presenter.toJson(primaryItems))
        }
        platforms_refresh.isRefreshing = false
    }

    override fun onRoute(locationEvents: List<LocationEvent>) {
        platforms_map.setRoute(presenter.toJson(locationEvents))
    }

    override fun onLocationState(state: LocationSettingsStates?) {
        super.onLocationState(state)
        onLocationAvailability(state?.isGpsUsable == true)
    }

    override fun onLocationAvailability(available: Boolean) {
        platforms_map.changeIcon(available)
    }

    override fun onLocationResult(location: SimpleLocation) {
        platforms_map.setLocation(location)
        locationCount++
        if (locationCount % 2 == 0L) {
            return
        }
        platformsAdapter.apply {
            primaryItems.apply {
                notifyItems(true, null, location)
                sortBy { it.meters }
            }
            items.notifyItems(false, null, location)
            notifyDataSetChanged()
        }
    }

    override fun cancelWork() {
        presenter.cancelExit(applicationContext)
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
                    SendWorker.launch(applicationContext)
                }
            }
        }
    }

    override fun onDestroy() {
        waitDialog?.dismiss()
        platforms_map.release()
        super.onDestroy()
    }

    /**
     * The order of notification (primary) -> (secondary) is important
     */
    private fun ArrayList<PlatformContainers>.notifyItems(
        isPrimary: Boolean,
        platforms: List<PlatformContainers>? = null,
        location: SimpleLocation? = null,
        photoEvents: List<PhotoEvent>? = null,
        cleanEvents: List<CleanEvent>? = null
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
        if (photoEvents != null) {
            forEach {
                for (event in photoEvents) {
                    if (it.kpId == event.kpId) {
                        if (!isPrimary) {
                            val millis = event.whenTime.withZone(zone).millis
                            if (it.timestamp < millis) {
                                it.timestamp = millis
                            }
                        }
                        photoErrors.get(event.type)?.run {
                            it.addError(this, 0)
                        }
                    }
                }
            }
        }
        if (cleanEvents != null) {
            val iterator = listIterator()
            for (item in iterator) {
                for (event in cleanEvents) {
                    if (item.kpId == event.kpId) {
                        val eventTime = event.whenTime.withZone(zone)
                        if (!isPrimary) {
                            val millis = eventTime.millis
                            if (item.timestamp < millis) {
                                item.timestamp = millis
                            }
                        }
                        if (refreshTime?.withZone(zone)?.isBefore(eventTime) == true) {
                            if (event.isEmpty) {
                                item.status = PlatformStatus.NOT_CLEANED.id
                                if (isPrimary) {
                                    // because of this status the primary item should be in secondary items
                                    platformsAdapter.items.add(item)
                                    iterator.remove()
                                }
                            }
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