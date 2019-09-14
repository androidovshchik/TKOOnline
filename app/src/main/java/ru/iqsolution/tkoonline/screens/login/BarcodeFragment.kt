package ru.iqsolution.tkoonline.screens.login

import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.jetbrains.anko.UI
import ru.iqsolution.tkoonline.screens.BaseFragment
import java.io.IOException

class BarcodeFragment : BaseFragment() {

    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private lateinit var cameraView: SurfaceView
    private lateinit var barcodeValue: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            verticalLayout {
                linearLayout {
                    // ...
                }
                linearLayout {
                    // ...
                }
            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1600, 1024)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
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

        barcodeDetector.setProcessor(object : Detector.Processor {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<*>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    barcodeValue.post(Runnable {
                        //Update barcode value to TextView
                        barcodeValue.setText(barcodes.valueAt(0).displayValue)
                    })
                }
            }
        })
    }
}