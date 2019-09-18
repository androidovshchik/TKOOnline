package ru.iqsolution.tkoonline.screens.containers

import android.os.Bundle
import com.yandex.mapkit.MapKitFactory
import kotlinx.android.synthetic.main.activity_containers.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.MAPKIT_KEY
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.data.models.PhotoItem
import ru.iqsolution.tkoonline.screens.BaseActivity

class ContainersActivity : BaseActivity(), ContainersContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_KEY)
        MapKitFactory.initialize(applicationContext)
        setContentView(R.layout.activity_containers)
        containers_complete.onClick {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        containers_map.onStart()
    }

    override fun onReceivedTypes(data: List<PhotoItem>) {

    }

    override fun onReceivedContainers(data: List<ContainerItem>) {

    }

    override fun onStop() {
        containers_map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onBackPressed() {}
    /*private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_containers)
        /**
         * Initialize the library to load required native libraries.
         * It is recommended to initialize the MapKit library in the Activity.onCreate method
         * Initializing in the Application.onCreate method may lead to extra calls and increased battery use.
         */
        // Now MapView can be created.
        setContentView(R.layout.map)
        super.onCreate(savedInstanceState)
        mapView = findViewById<View>(R.id.mapview)

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }*/
}