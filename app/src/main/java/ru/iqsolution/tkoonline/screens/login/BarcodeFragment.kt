package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.jetbrains.anko.*
import ru.iqsolution.tkoonline.DANGER_PERMISSIONS
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.screens.BaseFragment
import timber.log.Timber
import java.io.IOException

@Suppress("DEPRECATION")
class BarcodeFragment : BaseFragment() {

    private lateinit var barcodeDetector: BarcodeDetector

    private lateinit var cameraSource: CameraSource

    private lateinit var cameraView: SurfaceView

    private var isActive = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        barcodeDetector = BarcodeDetector.Builder(appContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                detections.apply {
                    if (detectorIsOperational()) {
                        detectedItems.forEach { _, barcode: Barcode ->
                            Timber.d(barcode.rawValue)
                        }
                    }
                }
            }

            override fun release() {}
        })
        cameraSource = CameraSource.Builder(appContext, barcodeDetector)
            .setRequestedPreviewSize(640, 480) // 4:3
            .setAutoFocusEnabled(true)
            .build()
        return UI {
            frameLayout {
                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
                cameraView = surfaceView {
                    holder.addCallback(object : SurfaceHolder.Callback {

                        override fun surfaceCreated(holder: SurfaceHolder) {
                            startPreview()
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            cameraSource.stop()
                        }
                    })
                }.lparams {
                    gravity = Gravity.CENTER
                }
            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (appContext?.areGranted(*DANGER_PERMISSIONS) != true) {
            requestPermissions(DANGER_PERMISSIONS, REQUEST_PERMISSIONS)
        }
    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    @SuppressLint("MissingPermission")
    private fun startPreview() {
        try {
            if (appContext?.areGranted(*DANGER_PERMISSIONS) == true) {
                cameraView.apply {
                    Timber.d("height " + height)
                    if (height <= 0) {
                        cameraSource.start()
                    } else {
                        Timber.d("start(cameraView.holder)")
                        cameraSource.start(holder)
                    }
                    Timber.d("previewSize " + cameraSource.previewSize)
                    //holder.setFixedSize(500, 281)
                    //
                    layoutParams = FrameLayout.LayoutParams(dip(250) * 3 / 4, dip(250)).apply {
                        gravity = Gravity.CENTER
                    }
                }
                //1280x720
                //cameraSource.previewSize
            }
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    override fun onStop() {
        isActive = false
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (isActive) {
                startPreview()
            }
        }
    }

    override fun onDestroyView() {
        barcodeDetector.release()
        cameraSource.release()
        super.onDestroyView()
    }

    companion object {

        private const val REQUEST_PERMISSIONS = 100
    }
}