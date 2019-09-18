package ru.iqsolution.tkoonline.screens.containers

import android.os.Bundle
import com.yandex.mapkit.MapKitFactory
import kotlinx.android.synthetic.main.activity_containers.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.MAPKIT_KEY
import ru.iqsolution.tkoonline.R
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
    }

    override fun onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }*/
}