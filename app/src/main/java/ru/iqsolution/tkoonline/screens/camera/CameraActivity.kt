package ru.iqsolution.tkoonline.screens.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
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

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        setContentView(R.layout.activity_camera)
        cameraExecutor = ContextCompat.getMainExecutor(applicationContext)
        toggleLight(preferences.enableLight)
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
            imageCapture?.let {
                val output = ImageCapture.OutputFileOptions.Builder(File(intent.getStringExtra(EXTRA_PHOTO_PATH)!!))
                    .setMetadata(ImageCapture.Metadata().also {
                        it.location = preferences.location
                    })
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

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        camera?.cameraControl?.enableTorch(false)
        scanFile(intent.getStringExtra(EXTRA_PHOTO_PATH)!!)
        setResult(RESULT_OK)
        finish()
    }

    override fun onError(exception: ImageCaptureException) {
        Timber.e(exception)
    }

    override fun onBackPressed() {
        camera?.cameraControl?.enableTorch(false)
        finish()
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        CameraX.unbindAll()
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    private fun scanFile(path: String) {
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = Uri.parse("file://$path")
        })
        MediaScannerConnection.scanFile(applicationContext, arrayOf(path), null, null)
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