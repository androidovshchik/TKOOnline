package ru.iqsolution.tkoonline.screens.qrcode

import android.annotation.SuppressLint
import android.content.Context
import android.view.SurfaceHolder
import com.google.android.gms.common.images.Size
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
class QrCodeManager(listener: QrCodeListener) {

    private val reference = WeakReference(listener)

    private lateinit var barcodeDetector: BarcodeDetector

    private lateinit var cameraSource: CameraSource

    fun init(context: Context) {
        barcodeDetector = BarcodeDetector.Builder(context)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()
        barcodeDetector.setProcessor(processor)
        cameraSource = CameraSource.Builder(context, barcodeDetector)
            .setRequestedPreviewSize(640, 480) // 4:3
            .setAutoFocusEnabled(true)
            .build()
    }

    @SuppressLint("MissingPermission")
    fun start(holder: SurfaceHolder): Size? {
        try {
            cameraSource.start(holder)
            return cameraSource.previewSize
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    fun stop() {
        cameraSource.stop()
    }

    fun release() {
        barcodeDetector.release()
        cameraSource.release()
    }

    private val processor = object : Detector.Processor<Barcode> {

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            detections.apply {
                if (detectorIsOperational()) {
                    if (detectedItems.size() > 0) {
                        reference.get()?.onQrCode(detectedItems.valueAt(0).rawValue ?: "")
                    }
                }
            }
        }

        override fun release() {}
    }
}
