package ru.iqsolution.tkoonline.screens.camera

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.android.synthetic.main.activity_camera.*
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import timber.log.Timber
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Returns [android.app.Activity.RESULT_OK] if photo was captured
 */
class CameraActivity : BaseActivity<CameraPresenter>(), CameraContract.View, LifecycleOwner {

    override val presenter: CameraPresenter by instance()

    private lateinit var mainExecutor1: Executor

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    override fun getLifecycle() = lifecycleRegistry

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mainExecutor1 = ContextCompat.getMainExecutor(applicationContext)
        turn_light.setOnClickListener {

        }
        shot.setOnClickListener {

        }
        camera_preview.post {

            // Keep track of the display in which this view is attached
            displayId = camera_preview.display.displayId
            bindCameraUseCases()
        }
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    private fun bindCameraUseCases() {
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { camera_preview.display.getRealMetrics(it) }
        Timber.e("Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Timber.e("Preview aspect ratio: $screenAspectRatio")

        val rotation = camera_preview.display.rotation

        // Bind the CameraProvider to the LifeCycleOwner
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation
                .setTargetRotation(rotation)
                .build()

            // Default PreviewSurfaceProvider
            preview?.setSurfaceProvider(camera_preview.previewSurfaceProvider)

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits requested capture mode
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_ON)
                .build()

            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()

            try {
                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                camera?.cameraControl?.enableTorch(true)
            } catch (exc: Exception) {
                Timber.e("Use case binding failed", exc)
            }

        }, mainExecutor1)
    }

    public override fun onStart() {
        super.onStart()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    /**
     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}