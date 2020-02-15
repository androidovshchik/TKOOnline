package ru.iqsolution.tkoonline.screens.camera

import android.os.Bundle
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseActivity

/**
 * Returns [android.app.Activity.RESULT_OK] if photo was captured
 */
class CameraActivity : BaseActivity<CameraPresenter>(), CameraContract.View {

    override val presenter: CameraPresenter by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }
}