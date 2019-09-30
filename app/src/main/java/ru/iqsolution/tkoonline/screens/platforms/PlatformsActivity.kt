package ru.iqsolution.tkoonline.screens.platforms

import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_platforms.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_PLATFORM
import ru.iqsolution.tkoonline.EXTRA_TYPES
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.platform.PlatformActivity
import ru.iqsolution.tkoonline.services.TelemetryService

class PlatformsActivity : BaseActivity<PlatformsPresenter>(), PlatformsContract.View,
    BaseAdapter.Listener<PlatformContainers> {

    private lateinit var platformsAdapter: PlatformsAdapter

    private val photoTypes = arrayListOf<PhotoType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platforms)
        presenter = PlatformsPresenter(application).also {
            it.attachView(this)
        }
        platforms_map.apply {
            loadUrl(URL)
            preferences.apply {
                lastTime?.let {
                    setLocation(lastLat.toDouble(), lastLon.toDouble())
                }
            }
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
            platformsAdapter = PlatformsAdapter(applicationContext).apply {
                setAdapterListener(this@PlatformsActivity)
            }
            adapter = platformsAdapter
        }
        platforms_complete.onClick {
            platforms_map.clearState(true)
            TelemetryService.stop(applicationContext)
            presenter.apply {
                clearAuthorization()
                detachView()
            }
            startActivityNoop<LoginActivity>()
            finish()
        }
        if (preferences.allowPhotoRefKp) {
            platforms_placeholder.visibility = View.VISIBLE
            platforms_photo.apply {
                visibility = View.VISIBLE
                onClick {

                }
            }
        }
    }

    override fun onReceivedTypes(data: List<PhotoType>) {
        photoTypes.apply {
            clear()
            addAll(data)
        }
    }

    override fun changeMapPosition(latitude: Double, longitude: Double) {
        platforms_map.moveTo(latitude, longitude)
    }

    override fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>) {
        platformsAdapter.apply {
            primaryItems.apply {
                clear()
                addAll()
            }
            items.clear()
            primary.forEach {
                primaryItems.add(it)
            }
            secondary.forEach {
                items.add(it)
            }
            notifyDataSetChanged()
        }
        platforms_refresh.isRefreshing = false
    }

    /**
     * Primary platforms will overlay secondary
     */
    override fun updateMapMarkers(primary: String, secondary: String) {
        platforms_map.setMarkers(secondary, primary)
    }

    override fun onLocationResult(location: Location) {
        platforms_map.setLocation(location.latitude, location.longitude)
        // todo sort primary
    }

    override fun onAdapterEvent(position: Int, item: PlatformContainers, param: Any?) {
        startActivityNoop<PlatformActivity>(
            null,
            EXTRA_TYPES to photoTypes,
            EXTRA_PLATFORM to presenter.formatPlatform(item)
        )
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        platforms_map.release()
        super.onDestroy()
    }

    companion object {

        private const val URL = "file:///android_asset/platforms.html"
    }
}