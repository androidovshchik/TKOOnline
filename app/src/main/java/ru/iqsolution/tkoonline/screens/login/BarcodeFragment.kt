package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.view.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.BaseFragment
import java.io.IOException

class BarcodeFragment : BaseFragment() {

    private lateinit var barcodeDetector: BarcodeDetector

    private lateinit var cameraSource: CameraSource

    private lateinit var cameraView: SurfaceView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val width = appContext?.resources?.getDimensionPixelSize(R.dimen.barcode_width) ?: 1600
        val height = appContext?.resources?.getDimensionPixelSize(R.dimen.barcode_height) ?: 1024
        barcodeDetector = BarcodeDetector.Builder(appContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(appContext, barcodeDetector)
            .setRequestedPreviewSize(width, height)
            .setAutoFocusEnabled(true)
            .build()
        cameraView = SurfaceView(appContext).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            holder.addCallback(object : SurfaceHolder.Callback {

                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {

                        cameraSource.start(cameraView.holder)
                    } catch (ex: IOException) {
                        ex.printStackTrace()
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
        barcodeDetector.setProcessor(object : Detector.Processor {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<*>) {

            }
        })
    }

    override fun onDestroyView() {
        barcodeDetector.release()
        cameraSource.release()
        super.onDestroyView()
    }
}