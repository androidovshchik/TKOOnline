package ru.iqsolution.tkoonline.screens.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import kotlinx.android.synthetic.main.activity_camera.*
import org.kodein.di.generic.instance
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

    private val imageSavedListener = object : ImageCapture.OnImageSavedCallback {
        override fun onError(imageCaptureError: Int, message: String, cause: Throwable?) {
            Log.e(TAG, "Photo capture failed: $message", cause)
        }

        override fun onImageSaved(photoFile: File) {
            Log.d(TAG, "Photo capture succeeded: ${photoFile.absolutePath}")

            // We can only change the foreground Drawable using API level 23+ API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Update the gallery thumbnail with latest picture taken
                setGalleryThumbnail(photoFile)
            }

            // Implicit broadcasts will be ignored for devices running API level >= 24
            // so if you only target API level 24+ you can remove this statement
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                requireActivity().sendBroadcast(
                    Intent(android.hardware.Camera.ACTION_NEW_PICTURE, Uri.fromFile(photoFile))
                )
            }

            // If the folder selected is an external media directory, this is unnecessary
            // but otherwise other apps will not be able to access our images unless we
            // scan them using [MediaScannerConnection]
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(photoFile.extension)
            MediaScannerConnection.scanFile(
                context, arrayOf(photoFile.absolutePath), arrayOf(mimeType), null
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        setContentView(R.layout.activity_camera)
        cameraExecutor = ContextCompat.getMainExecutor(applicationContext)
        toggleLightIcon(preferences.enableLight)
        turn_light.setOnClickListener {
            camera?.let {

            }
        }
        shot.setOnClickListener {
            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
                val metadata = ImageCapture.Metadata().apply {

                    // Mirror image when using the front camera
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }
                imageCapture.takePicture(photoFile, metadata, cameraExecutor, imageSavedListener)
            }
        }
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
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

            // CameraProvider
            val cameraProvider = cameraProvider.get()

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
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                //camera?.cameraControl?.enableTorch(true)
            } catch (exc: Exception) {
                Timber.e("Use case binding failed", exc)
            }
        }, cameraExecutor)
    }

    private fun toggleLightIcon(enable: Boolean) {
        turn_light.setImageResource(
            if (enable) {
                R.drawable.ic_highlight_on
            } else {
                R.drawable.ic_highlight_off
            }
        )
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
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