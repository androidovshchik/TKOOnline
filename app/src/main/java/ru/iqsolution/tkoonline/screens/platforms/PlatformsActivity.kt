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
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity
import ru.iqsolution.tkoonline.screens.platform.PlatformActivity

class PlatformsActivity : BaseActivity<PlatformsPresenter>(), PlatformsContract.View,
    AdapterListener<PlatformContainers> {

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

    override fun changeMapPosition(location: SimpleLocation) {
        platforms_map.moveTo(location)
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

    override fun onLoggedOut() {
        startActivityNoop<LoginActivity>()
        finish()
    }

    override fun onLocationResult(location: SimpleLocation) {
        platforms_map.setLocation(location)
        platformsAdapter.apply {
            notifyPrimaryItems(location)
            notifySecondaryItems(location)
            notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PLATFORM) {
            if (resultCode == RESULT_OK) {
                platformsAdapter.apply {
                    items.apply {
                        val id = data?.getIntExtra(EXTRA_PLATFORMS_KP_ID, -1) ?: -1
                        firstOrNull { it.kpId == id }?.let {
                            remove(it)
                            it.errors.apply {
                                clear()
                                addAll(data?.getStringArrayListExtra(EXTRA_PLATFORMS_ERRORS).orEmpty())
                            }
                            add(0, it)
                            // todo also map changes
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
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

        private const val URL = "file:///android_asset/platforms.html"
    }
}