package ru.iqsolution.tkoonline.screens.platforms

import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_platforms.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_ID
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.local.entities.PlatformContainersPhotoClean
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.platform.PlatformActivity
import ru.iqsolution.tkoonline.services.TelemetryService

class PlatformsActivity : BaseActivity<PlatformsPresenter>(), PlatformsContract.View, BaseAdapter.Listener<Platform> {

    private lateinit var platformsAdapter: PlatformsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platforms)
        presenter = PlatformsPresenter(application).also {
            it.attachView(this)
        }
        platforms_map.loadUrl("file:///android_asset/platforms.html")
        platforms_refresh.setOnRefreshListener {
            presenter.loadPlatforms()
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
            logout()
        }
        if (presenter.isAllowedPhotoKp) {
            platforms_placeholder.visibility = View.VISIBLE
            platforms_photo.apply {
                visibility = View.VISIBLE
                onClick {

                }
            }
        }
    }

    override fun updateListMarkers(
        primary: List<PlatformContainersPhotoClean>,
        secondary: List<PlatformContainersPhotoClean>
    ) {
        platformsAdapter.apply {
            primaryItems.clear()
            items.clear()
            primary.forEach {
                primaryItems.add(it)
                addMark(it)
            }
            secondary.forEach {
                items.add(it)
                addMark(it)
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

    override fun changeMapPosition(latitude: Double, longitude: Double) {
        platforms_map.moveTo(latitude, longitude)
    }

    override fun onLocationResult(location: Location) {
        platforms_map.setLocation(location.latitude, location.longitude)
    }

    override fun onAdapterEvent(position: Int, item: Platform, param: Any?) {
        startActivityNoop<PlatformActivity>(
            null,
            EXTRA_ID to item.kpId
        )
    }

    private fun logout() {
        platforms_map.clearState(true)
        TelemetryService.stop(applicationContext)
        presenter.clearAuthorization()
        startActivityNoop<LoginActivity>()
        finish()
    }

    override fun onBackPressed() {}
}