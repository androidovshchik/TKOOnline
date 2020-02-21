package ru.iqsolution.tkoonline.screens.login.qrcode

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import org.jetbrains.anko.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.screens.base.BaseFragment
import ru.iqsolution.tkoonline.screens.login.LoginContract

@Suppress("DEPRECATION")
class QrCodeFragment : BaseFragment() {

    private var scannerManager: ScannerManager? = null

    private lateinit var cameraView: SurfaceView

    private var alertDialog: AlertDialog? = null

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

    @SuppressLint("BatteryLife")
    private fun checkPermissions(): Boolean {
        val context = context ?: return false
        // NOTICE this violates Google Play policy
        if (!context.powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            startActivityForResult(
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.fromParts("package", context.packageName, null)
                ), REQUEST_BATTERY
            )
            return false
        }
        if (!context.areGranted(*DANGER_PERMISSIONS)) {
            DANGER_PERMISSIONS.forEach {
                if (shouldShowRequestPermissionRationale(it)) {
                    promptUser("Пожалуйста, предоставьте все разрешения", Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    return false
                }
            }
            requestPermissions(DANGER_PERMISSIONS, REQUEST_PERMISSIONS)
            return false
        }
        if (isOreoPlus()) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                promptUser("Пожалуйста, разрешите установку обновлений", Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                return false
            }
        }
        makeCallback<LoginContract.View> {
            onCanUpdate()
        }
        return true
    }

    private fun promptUser(message: String, action: String) {
        alertDialog = alert(message) {
            isCancelable = false
            positiveButton("Открыть") {
                startActivity(Intent(action, Uri.fromParts("package", context?.packageName, null)))
            }
        }.show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_BATTERY) {
            if (isActive) {
                startPreview()
            }
        }
    }

    override fun onDestroyView() {
        alertDialog?.dismiss()
        scannerManager?.destroy()
        super.onDestroyView()
    }

    companion object {

        private const val REQUEST_PERMISSIONS = 100

        private const val REQUEST_BATTERY = 110

        private val DANGER_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}