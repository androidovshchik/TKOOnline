package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.util.forEach
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.jetbrains.anko.collections.forEach
import org.jetbrains.anko.matchParent
import ru.iqsolution.tkoonline.DANGER_PERMISSIONS
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.screens.BaseFragment
import timber.log.Timber
import java.io.IOException

class BarcodeFragment : BaseFragment() {

    private lateinit var barcodeDetector: BarcodeDetector

    private lateinit var cameraSource: CameraSource

    private lateinit var cameraView: SurfaceView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val width = appContext?.resources?.getDimensionPixelSize(R.dimen.barcode_width) ?: 1600
        barcodeDetector = BarcodeDetector.Builder(appContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(appContext, barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()
        cameraView = SurfaceView(appContext).apply {
            layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
            holder.addCallback(object : SurfaceHolder.Callback {

                @SuppressLint("MissingPermission")
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (appContext?.areGranted(*DANGER_PERMISSIONS) == true) {
                            cameraSource.start(holder)
                            cameraSource.previewSize
                        }
                    } catch (e: IOException) {
                        Timber.e(e)
                    }
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    cameraSource.stop()
                }
            })
        }
        return cameraView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {

            @Suppress("DEPRECATION")
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
    }

    override fun onDestroyView() {
        barcodeDetector.release()
        cameraSource.release()
        super.onDestroyView()
    }
}