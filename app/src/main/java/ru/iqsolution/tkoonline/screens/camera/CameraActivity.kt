package ru.iqsolution.tkoonline.screens.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.toast
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.EXTRA_PHOTO_PATH
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.windowSize
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import timber.log.Timber
import java.io.File
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

    private lateinit var externalPhoto: File

    private var preFinishing = false

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        setContentView(R.layout.activity_camera)
        cameraExecutor = ContextCompat.getMainExecutor(applicationContext)
        externalPhoto = File(intent.getStringExtra(EXTRA_PHOTO_PATH)!!)
        val scaleGestureDetector =
            ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    camera?.run {
                        val zoomRatio = cameraInfo.zoomState.value?.zoomRatio ?: 0f
                        cameraControl.setZoomRatio(zoomRatio * detector.scaleFactor)
                    }
                    return true
                }
            })
        camera_preview.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP && !preFinishing) {
                camera?.let {
                    val factory = SurfaceOrientedMeteringPointFactory(
                        camera_preview.width.toFloat(), camera_preview.height.toFloat()
                    )
                    val point = factory.createPoint(event.x, event.y)
                    it.cameraControl.startFocusAndMetering(
                        FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                            .disableAutoCancel()
                            .build()
                    )
                }
            }
            return@setOnTouchListener true
        }
        toggleLight(preferences.enableLight)
        turn_light.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            camera?.let {
                val toggled = when (it.cameraInfo.torchState.value) {
                    TorchState.ON -> toggleLight(false)
                    else -> toggleLight(true)
                }
                if (toggled == false) {
                    toast("Требуется наличие вспышки для камеры")
                }
            }
        }
        shot.setOnClickListener {
            if (preFinishing) {
                return@setOnClickListener
            }
            imageCapture?.let {
                preFinishing = true
                val output = ImageCapture.OutputFileOptions.Builder(externalPhoto)
                    .setMetadata(ImageCapture.Metadata())
                    .build()
                it.takePicture(output, cameraExecutor, this)
            }
        }
        val cameraProvider = ProcessCameraProvider.getInstance(applicationContext)
        cameraProvider.addListener(Runnable {
            val window = windowManager.windowSize
            val aspectRatio = getAspectRatio(window.x, window.y)
            val preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(Surface.ROTATION_0)
                .build()
            preview.setSurfaceProvider(camera_preview.previewSurfaceProvider)
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(Surface.ROTATION_0)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            try {
                camera = cameraProvider.get()
                    .bindToLifecycle(this, cameraSelector, preview, imageCapture)
                toggleLight(preferences.enableLight)
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }, cameraExecutor)
    }

    private fun toggleLight(enable: Boolean): Boolean? {
        camera?.also {
            if (!it.cameraInfo.hasFlashUnit()) {
                return false
            }
            it.cameraControl.enableTorch(enable)
            imageCapture?.flashMode = if (enable) {
                ImageCapture.FLASH_MODE_ON
            } else {
                ImageCapture.FLASH_MODE_OFF
            }
            preferences.enableLight = enable
        }
        turn_light.setImageResource(
            if (enable) R.drawable.ic_highlight_on else R.drawable.ic_highlight_off
        )
        return null
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    @Suppress("DEPRECATION")
    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        camera?.cameraControl?.enableTorch(false)
        setResult(RESULT_OK)
        finish()
    }

    override fun onError(exception: ImageCaptureException) {
        preFinishing = false
        Timber.e(exception)
        showError(exception)
    }

    override fun onBackPressed() {
        if (!preFinishing) {
            camera?.cameraControl?.enableTorch(false)
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        CameraX.unbindAll()
        super.onDestroy()
    }

    companion object {

        private fun getAspectRatio(width: Int, height: Int): Int {
            val previewRatio = max(width, height).toDouble() / min(width, height)
            return if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0)) {
                AspectRatio.RATIO_4_3
            } else {
                AspectRatio.RATIO_16_9
            }
        }
    }
}