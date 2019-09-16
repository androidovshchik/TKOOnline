package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.jetbrains.anko.UI
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.surfaceView
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
        //val width = appContext?.resources?.getDimensionPixelSize(R.dimen.barcode_width) ?: 1024
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
            .setAutoFocusEnabled(true)
            .build()
        return UI {
            cameraView = surfaceView {
                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
                holder.addCallback(object : SurfaceHolder.Callback {

                    override fun surfaceCreated(holder: SurfaceHolder) {
                        startPreview()
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        cameraSource.stop()
                    }
                })
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
                cameraSource.start(cameraView.holder)
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