package ru.iqsolution.tkoonline.screens.container

import android.os.Bundle
import com.yandex.mapkit.MapKitFactory
import kotlinx.android.synthetic.main.activity_containers.*
import ru.iqsolution.tkoonline.MAPKIT_KEY
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseActivity

class ContainerActivity : BaseActivity<ContainerPresenter>(), ContainerContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_KEY)
        MapKitFactory.initialize(applicationContext)
        setContentView(R.layout.activity_container)
        presenter = ContainerPresenter(application).also {
            it.attachView(this)
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        containers_map.onStart()
    }

    override fun onStop() {
        containers_map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onBackPressed() {}
}