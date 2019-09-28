package ru.iqsolution.tkoonline.screens.platforms

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
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import ru.iqsolution.tkoonline.screens.base.BaseAdapter
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.platform.PlatformActivity

class PlatformsActivity : BaseActivity<PlatformsPresenter>(), PlatformsContract.View, BaseAdapter.Listener<Platform> {

    override val attachService = true

    private lateinit var platformsAdapter: PlatformsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platforms)
        presenter = PlatformsPresenter(application).also {
            it.attachView(this)
        }
        platforms_map.loadUrl("file:///android_asset/platforms.html")
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
            presenter.clearAuthorization()
            startActivityNoop<LoginActivity>()
            finish()
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

    override fun onAdapterEvent(position: Int, item: Platform, param: Any?) {
        startActivityNoop<PlatformActivity>(
            null,
            EXTRA_ID to item.kpId
        )
    }

    override fun onBackPressed() {}
}