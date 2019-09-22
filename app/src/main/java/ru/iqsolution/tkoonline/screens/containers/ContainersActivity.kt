package ru.iqsolution.tkoonline.screens.containers

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.android.synthetic.main.activity_containers.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_ID
import ru.iqsolution.tkoonline.MAPKIT_KEY
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.data.models.PhotoItem
import ru.iqsolution.tkoonline.extensions.getVectorBitmap
import ru.iqsolution.tkoonline.extensions.startActivitySimply
import ru.iqsolution.tkoonline.screens.BaseActivity
import ru.iqsolution.tkoonline.screens.container.ContainerActivity
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.widgets.ContainerView

class ContainersActivity : BaseActivity<ContainersPresenter>(), ContainersContract.View {

    private lateinit var containersAdapter: ContainersAdapter

    private lateinit var objects: MapObjectCollection

    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_KEY)
        MapKitFactory.initialize(applicationContext)
        setContentView(R.layout.activity_containers)
        presenter = ContainersPresenter(application).also {
            it.attachView(this)
            it.receiveData()
        }
        bitmap = getVectorBitmap(R.drawable.ic_delete)
        containers_map.map.apply {
            objects = mapObjects.addCollection()
        }
        containers_plus.onClick {
            containers_map.map.apply {
                move(
                    CameraPosition(cameraPosition.target, cameraPosition.zoom + 1, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f), null
                )
            }
        }
        containers_minus.onClick {
            containers_map.map.apply {
                move(
                    CameraPosition(cameraPosition.target, cameraPosition.zoom - 1, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f), null
                )
            }
        }
        containers_location.onClick {

        }
        containers_list.apply {
            addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL).apply {
                ContextCompat.getDrawable(applicationContext, R.drawable.divider)?.let {
                    setDrawable(it)
                }
            })
            containersAdapter = ContainersAdapter(applicationContext).apply {
                setAdapterListener { _, item, _ ->
                    startActivitySimply<ContainerActivity>(
                        null,
                        EXTRA_ID to item.kpId
                    )
                }
            }
            adapter = containersAdapter
        }
        containers_complete.onClick {
            presenter.clearAuthorization()
            startActivitySimply<LoginActivity>()
            finish()
        }
        if (presenter.isAllowedPhotoKp) {
            containers_placeholder.visibility = View.VISIBLE
            containers_photo.apply {
                visibility = View.VISIBLE
                onClick {

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        containers_map.onStart()
    }

    override fun onReceivedTypes(data: List<PhotoItem>) {

    }

    override fun onReceivedContainers(first: List<ContainerItem>, other: List<ContainerItem>, center: Point?) {
        objects.clear()
        containersAdapter.apply {
            firstItems.clear()
            items.clear()
            first.forEach {
                firstItems.add(it)
                addMark(it)
            }
            other.forEach {
                items.add(it)
                addMark(it)
            }
            notifyDataSetChanged()
        }
        center?.let {
            containers_map.map.move(
                CameraPosition(it, 12.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )
        }
    }

    private fun addMark(item: ContainerItem) {
        objects.addPlacemark(Point(item.latitude, item.longitude)).apply {
            setView(ViewProvider(ContainerView(applicationContext).apply {
                init(item.status.color, null, bitmap)
            }))
        }
    }

    override fun onStop() {
        containers_map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        bitmap?.recycle()
        super.onDestroy()
    }
}