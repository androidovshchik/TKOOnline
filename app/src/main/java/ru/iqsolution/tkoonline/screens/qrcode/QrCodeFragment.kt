package ru.iqsolution.tkoonline.screens.qrcode

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import org.jetbrains.anko.UI
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.surfaceView
import ru.iqsolution.tkoonline.DANGER_PERMISSIONS
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.screens.base.BaseFragment

@Suppress("DEPRECATION")
class QrCodeFragment : BaseFragment() {

    private var scannerManager: ScannerManager? = null

    private lateinit var cameraView: SurfaceView

    private var maxSize = 0

    private var isActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maxSize = context.resources.getDimensionPixelSize(R.dimen.barcode_max_size)
        makeCallback<ScannerListener> {
            scannerManager = ScannerManager(context, this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                            scannerManager?.stop()
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
        if (context?.areGranted(*DANGER_PERMISSIONS) != true) {
            return
        }
        cameraView.apply {
            scannerManager?.start(holder)?.let {
                // NOTICE supported only the portrait orientation
                layoutParams = FrameLayout.LayoutParams(maxSize * it.height / it.width, maxSize).apply {
                    gravity = Gravity.CENTER
                }
            }
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
        scannerManager?.destroy()
        super.onDestroyView()
    }

    companion object {

        private const val REQUEST_PERMISSIONS = 100
    }
}