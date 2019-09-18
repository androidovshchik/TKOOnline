package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.jetbrains.anko.UI
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.surfaceView
import ru.iqsolution.tkoonline.DANGER_PERMISSIONS
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.screens.BaseFragment
import timber.log.Timber

@Suppress("DEPRECATION")
class QrCodeFragment : BaseFragment() {

    private lateinit var barcodeDetector: BarcodeDetector

    private lateinit var cameraSource: CameraSource

    private lateinit var cameraView: SurfaceView

    private var isActive = false

    private var maxSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        context?.resources?.getDimensionPixelSize(R.dimen.barcode_max_size)?.let {
            maxSize = it
        }
        barcodeDetector = BarcodeDetector.Builder(context)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                detections.apply {
                    if (detectorIsOperational()) {
                        if (detectedItems.size() > 0) {
                            activity?.let {
                                if (it is LoginActivity) {
                                    it.onQrCode(detectedItems.valueAt(0).rawValue ?: "")
                                }
                            }
                        }
                    }
                }
            }

            override fun release() {}
        })
        cameraSource = CameraSource.Builder(context, barcodeDetector)
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
                }.lparams()
            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (context?.areGranted(*DANGER_PERMISSIONS) != true) {
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
            if (context?.areGranted(*DANGER_PERMISSIONS) == true) {
                cameraView.apply {
                    cameraSource.start(holder)
                    if (maxSize > 0) {
                        // NOTICE supported only the portrait orientation
                        val size = cameraSource.previewSize
                        layoutParams = FrameLayout.LayoutParams(maxSize * size.height / size.width, maxSize).apply {
                            gravity = Gravity.CENTER
                        }
                    }
                }
            }
        } catch (e: Exception) {
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