package ru.iqsolution.tkoonline.screens.camera

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.toast
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.windowSize
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import timber.log.Timber
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Returns [android.app.Activity.RESULT_OK] if photo was captured
 */
class CameraActivity : BaseActivity<CameraPresenter>(), CameraContract.View {

    override val presenter: CameraPresenter by instance()

    private var camera: Camera? = null

    private lateinit var cameraExecutor: Executor

    private var imageCapture: ImageCapture? = null

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        setContentView(R.layout.activity_camera)
        cameraExecutor = ContextCompat.getMainExecutor(applicationContext)
        turn_light.setOnClickListener {
            when (camera?.cameraInfo?.torchState?.value) {
                TorchState.ON -> toggleLight(false)
                else -> toggleLight(true)
            }
        }
        shot.setOnClickListener {
            imageCapture?.let {
                val photoFile = creatseFile(outputDirectory, FILENssAME, PHOTO_EXTssENSION)
                val metadata = ImageCapture.Metadata().apply {

                    // Mirror image when using the front camera
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }
                it.takePicture(metadata, cameraExecutor, this)
            }
        }
        val cameraProvider = ProcessCameraProvider.getInstance(applicationContext)
        cameraProvider.addListener(Runnable {
            val window = windowManager.windowSize
            val screenAspectRatio = aspectRatio(window.x, window.y)
            val previewRatio = max(width, height).toDouble() / min(width, height)
            if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
                return AspectRatio.RATIO_4_3
            }
            AspectRatio.RATIO_16_9
            Timber.e("Preview aspect ratio: $screenAspectRatio")

            val rotation = 0
            Timber.e("rotation $rotation")

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            // Preview
            val preview = Preview.Builder()
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
            try {
                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                camera = cameraProvider.get().bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                toggleLight(preferences.enableLight)
                //camera?.cameraControl?.enableTorch(true)
            } catch (exc: Exception) {
                Timber.e("Use case binding failed", exc)
            }
        }, cameraExecutor)
    }

    private fun toggleLight(enable: Boolean) {
        camera?.also {
            if (it.cameraInfo.hasFlashUnit()) {
                it.cameraControl.enableTorch(enable)
                preferences.enableLight = enable
                turn_light.setImageResource(
                    if (enable) R.drawable.ic_highlight_on else R.drawable.ic_highlight_off
                )
            } else {
                if (enable) {
                    toast("Требуется наличие вспышки для камеры")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        setResult(RESULT_OK)
        finish()
    }

    override fun onError(exception: ImageCaptureException) {
        Timber.e(exception)
    }

    override fun onBackPressed() {
        finish()
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        CameraX.unbindAll()
        super.onDestroy()
    }

    companion object {

        private const val RATIO_4_3 = 4.0 / 3.0

        private const val RATIO_16_9 = 16.0 / 9.0
    }
}