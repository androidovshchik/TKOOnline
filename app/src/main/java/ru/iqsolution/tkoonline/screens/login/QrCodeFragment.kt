package ru.iqsolution.tkoonline.screens.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telecom.TelecomManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_qr.*
import org.jetbrains.anko.powerManager
import org.jetbrains.anko.telecomManager
import org.kodein.di.instance
import ru.iqsolution.tkoonline.AdminManager
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.extensions.isGranted
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.extensions.isQPlus
import ru.iqsolution.tkoonline.screens.base.AppAlertDialog
import ru.iqsolution.tkoonline.screens.base.BaseFragment
import ru.iqsolution.tkoonline.screens.base.IBaseView

@Suppress("DEPRECATION")
class QrCodeFragment : BaseFragment() {

    private val adminManager: AdminManager by instance()

    private lateinit var codeScanner: CodeScanner

    private var alertDialog: AppAlertDialog? = null

    private val scanHandler = Handler()

    private val stopRunnable = Runnable {
        play.isVisible = true
        codeScanner.stopPreview()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        codeScanner = CodeScanner(activity, qr_scanner).apply {
            formats = listOf(BarcodeFormat.QR_CODE)
            zoom = 8
        }
        codeScanner.decodeCallback = DecodeCallback {
            activityCallback<LoginContract.View> {
                onQrCode(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activityCallback<IBaseView> {
                activity?.runOnUiThread {
                    showError(it)
                }
            }
        }
        play.setOnClickListener {
            startScan()
        }
        checkPermissions(REQUEST_PERMS)
    }

    override fun onResume() {
        super.onResume()
        if (context.isGranted(Manifest.permission.CAMERA)) {
            startScan()
        }
    }

    fun startScan() {
        play.isVisible = false
        scanHandler.removeCallbacks(stopRunnable)
        codeScanner.startPreview()
        scanHandler.postDelayed(stopRunnable, 30_000L)
    }

    @SuppressLint("BatteryLife")
    private fun checkPermissions(requestCode: Int) {
        val context = context ?: return
        val packageName = context.packageName
        when (requestCode) {
            REQUEST_PERMS -> {
                if (!context.areGranted(*DANGER_PERMISSIONS)) {
                    DANGER_PERMISSIONS.forEach {
                        if (shouldShowRequestPermissionRationale(it)) {
                            startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }, REQUEST_PERMS)
                            return
                        }
                    }
                    requestPermissions(DANGER_PERMISSIONS, REQUEST_PERMS)
                } else {
                    checkPermissions(REQUEST_DOZE)
                }
            }
            REQUEST_DOZE -> {
                if (!context.powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    // NOTICE this violates Google Play policy
                    startActivityForResult(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }, REQUEST_DOZE)
                } else {
                    checkPermissions(REQUEST_CALL)
                }
            }
            REQUEST_CALL -> {
                if (context.telecomManager.defaultDialerPackage != packageName) {
                    // RoleManager is not working for some reasons
                    startActivityForResult(Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                        putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                    }, REQUEST_CALL)
                } else {
                    checkPermissions(REQUEST_INSTALL)
                }
            }
            REQUEST_INSTALL -> {
                if (isOreoPlus()) {
                    if (!adminManager.isDeviceOwner) {
                        if (!context.packageManager.canRequestPackageInstalls()) {
                            startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }, REQUEST_INSTALL)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        scanHandler.removeCallbacks(stopRunnable)
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, perms: Array<out String>, results: IntArray) {
        if (context.isGranted(Manifest.permission.CAMERA)) {
            if (!codeScanner.isPreviewActive) {
                startScan()
            }
        }
        checkPermissions(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkPermissions(requestCode)
    }

    override fun onDestroyView() {
        alertDialog?.dismiss()
        super.onDestroyView()
    }

    companion object {

        private const val REQUEST_DOZE = 1
        private const val REQUEST_CALL = 2
        private const val REQUEST_INSTALL = 3

        private const val REQUEST_PERMS = 1000

        private val DANGER_PERMISSIONS = if (isQPlus()) arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) else arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}