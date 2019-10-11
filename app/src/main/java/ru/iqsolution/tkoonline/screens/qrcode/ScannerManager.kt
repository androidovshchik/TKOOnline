package ru.iqsolution.tkoonline.screens.qrcode

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import com.google.android.gms.common.images.Size
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.math.min

@Suppress("MemberVisibilityCanBePrivate")
class ScannerManager(context: Context, listener: ScannerListener) : Detector.Processor<Barcode> {

    private val reference = WeakReference(listener)

    private val barcodeDetector = BarcodeDetector.Builder(context)
        .setBarcodeFormats(Barcode.QR_CODE)
        .build()

    private val cameraSource: CameraSource

    init {
        barcodeDetector.setProcessor(this)
        cameraSource = CameraSource.Builder(context, barcodeDetector)
            .setRequestedPreviewSize(640, 480) // 4:3
            .setAutoFocusEnabled(true)
            .build()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    fun start(holder: SurfaceHolder): Size? {
        try {
            cameraSource.apply {
                start(holder)
                javaClass.getDeclaredField("zzg").apply {
                    isAccessible = true
                    (get(cameraSource) as Camera).apply {
                        startSmoothZoom(min(5, parameters.maxZoom))
                    }
                }
            }
            return cameraSource.previewSize
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return null
    }

    override fun receiveDetections(detections: Detector.Detections<Barcode>) {
        detections.apply {
            if (detectorIsOperational()) {
                if (detectedItems.size() > 0) {
                    reference.get()?.onQrCode(detectedItems.valueAt(0).rawValue ?: "")
                }
            }
        }
    }

    /**
     * Due to [Detector.Processor]
     */
    override fun release() {}

    fun stop() {
        cameraSource.stop()
    }

    fun destroy() {
        barcodeDetector.release()
        cameraSource.release()
    }
}
