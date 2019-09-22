package ru.iqsolution.tkoonline.screens.container

import android.os.Bundle
import com.yandex.mapkit.MapKitFactory
import kotlinx.android.synthetic.main.activity_containers.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.EXTRA_ID
import ru.iqsolution.tkoonline.MAPKIT_KEY
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.screens.BaseActivity

class ContainerActivity : BaseActivity<ContainerPresenter>(), ContainerContract.View {

    private var container: ContainerItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_KEY)
        MapKitFactory.initialize(applicationContext)
        setContentView(R.layout.activity_container)
        presenter = ContainerPresenter(application).also {
            it.attachView(this)
            container = it.getContainer(intent.getIntExtra(EXTRA_ID, -1))
        }
        toolbar_back.onClick {
            finish()
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