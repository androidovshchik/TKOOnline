package ru.iqsolution.tkoonline.screens.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.toast
import org.kodein.di.instance
import ru.iqsolution.tkoonline.EXTRA_PHOTO_PATH
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseActivity
import timber.log.Timber
import java.io.File

/**
 * Returns [android.app.Activity.RESULT_OK] if photo was captured
 */
class CameraActivity : BaseActivity<CameraContract.Presenter>(), CameraContract.View {

    override val presenter: CameraPresenter by instance()

    private var camera: Camera? = null

    private var cameraProvider: ProcessCameraProvider? = null

    private val cameraExecutor by lazy {
        ContextCompat.getMainExecutor(applicationContext)
    }

    private var imageCapture: ImageCapture? = null

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycleRegistry

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        setContentView(R.layout.activity_camera)
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
            if (event.action == MotionEvent.ACTION_UP) {
                camera?.let {
                    val factory = SurfaceOrientedMeteringPointFactory(
                        camera_preview.width.toFloat(), camera_preview.height.toFloat()
                    )
                    val point = factory.createPoint(event.x, event.y)
                    it.cameraControl.startFocusAndMetering(
                        FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
                    )
                }
            }
            return@setOnTouchListener true
        }
        turn_light.setOnClickListener {
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
            camera?.cameraControl?.enableTorch(false)?.addListener({
                imageCapture?.let {
                    setTouchable(false)
                    val file = File(intent.getStringExtra(EXTRA_PHOTO_PATH) ?: return@let)
                    val output = ImageCapture.OutputFileOptions.Builder(file)
                        .setMetadata(ImageCapture.Metadata())
                        .build()
                    it.takePicture(output, cameraExecutor, this)
                }
            }, cameraExecutor)
        }
        val cameraFuture = ProcessCameraProvider.getInstance(applicationContext)
        cameraFuture.addListener({
            cameraProvider = cameraFuture.get()
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(Surface.ROTATION_0)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()
            val orientationEventListener = object : OrientationEventListener(applicationContext) {

                override fun onOrientationChanged(orientation: Int) {
                    imageCapture?.targetRotation = when (orientation) {
                        in 45..134 -> Surface.ROTATION_270
                        in 135..224 -> Surface.ROTATION_180
                        in 225..314 -> Surface.ROTATION_90
                        else -> Surface.ROTATION_0
                    }
                }
            }
            orientationEventListener.enable()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(Surface.ROTATION_0)
                .build()
            preview.setSurfaceProvider(camera_preview.surfaceProvider)
            cameraProvider?.unbindAll()
            try {
                camera = cameraProvider
                    ?.bindToLifecycle(this, cameraSelector, preview, imageCapture)
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
        toggleLight(preferences.enableLight)
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onImageSaved(results: ImageCapture.OutputFileResults) {
        setResult(RESULT_OK)
        finish()
    }

    override fun onError(e: ImageCaptureException) {
        toggleLight(preferences.enableLight)
        setTouchable(true)
        Timber.e(e)
        showError(e)
    }

    override fun onBackPressed() {
        if (isTouchable) {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onStop() {
        camera?.cameraControl?.enableTorch(false)
        super.onStop()
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        cameraProvider?.unbindAll()
        super.onDestroy()
    }
}