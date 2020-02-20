package ru.iqsolution.tkoonline.screens.login.qrcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import org.jetbrains.anko.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.screens.base.BaseFragment
import timber.log.Timber

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
                            Timber.e("surfaceCreated")
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

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    @SuppressLint("MissingPermission")
    private fun startPreview() {
        if (!checkPermissions()) {
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

    private fun checkPermissions(): Boolean {
        if (context?.areGranted(*DANGER_PERMISSIONS) != true) {
            DANGER_PERMISSIONS.forEach {
                if (shouldShowRequestPermissionRationale(it)) {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context?.packageName, null)
                        )
                    )
                    longToast("Пожалуйста, предоставьте разрешения")
                    return false
                }
            }
            requestPermissions(DANGER_PERMISSIONS, REQUEST_PERMISSIONS)
            return false
        }
        return true
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

        private val DANGER_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}