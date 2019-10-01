package ru.iqsolution.tkoonline.screens.platforms

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_platforms.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_ID
import ru.iqsolution.tkoonline.EXTRA_PLATFORM
import ru.iqsolution.tkoonline.EXTRA_TYPES
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
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
        platformsAdapter = PlatformsAdapter(applicationContext).apply {
            setAdapterListener(this@PlatformsActivity)
        }
        presenter = PlatformsPresenter(application).apply {
            attachView(this@PlatformsActivity)
            saveAccessToken()
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
                    takePhoto()
                }
            }
        }
    }

    override fun onAdapterEvent(position: Int, item: PlatformContainers, param: Any?) {
        startActivityNoop<PlatformActivity>(
            REQUEST_PLATFORM,
            EXTRA_TYPES to photoTypes,
            EXTRA_PLATFORM to presenter.platformToJson(item)
        )
    }

    override fun onReceivedTypes(types: List<PhotoType>) {
        photoTypes.apply {
            clear()
            addAll(types)
        }
    }

    override fun changeMapPosition(location: SimpleLocation) {
        platforms_map.moveTo(location)
    }

    override fun onReceivedPrimary(platforms: List<PlatformContainers>) {
        platformsAdapter.apply {
            primaryItems.apply {
                clear()
                addAll(platforms)
            }
            preferences.location?.let {
                primaryItems.sortBy { it.meters }
            }
            notifyDataSetChanged()
        }
        platforms_refresh.isRefreshing = false
    }

    override fun onReceivedSecondary(platforms: List<PlatformContainers>) {
        platformsAdapter.apply {
            items.apply {
                clear()
                addAll(platforms)
            }
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
            primaryItems.forEach {

            }
            items.forEach {

            }
            preferences.location?.let {
                primaryItems.sortBy { it.meters }
            }
            notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PLATFORM) {
            if (resultCode == RESULT_OK) {
                platformsAdapter.apply {
                    primaryItems.apply {
                        val id = data?.getIntExtra(EXTRA_ID, -1) ?: -1
                        firstOrNull { it.kpId == id }?.let {
                            remove(it)
                            add(0, it)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        platforms_map.release()
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_PLATFORM = 1100

        private const val URL = "file:///android_asset/platforms.html"
    }
}