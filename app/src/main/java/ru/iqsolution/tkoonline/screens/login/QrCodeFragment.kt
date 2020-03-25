package ru.iqsolution.tkoonline.screens.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_qr.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.powerManager
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.AdminManager
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.areGranted
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import ru.iqsolution.tkoonline.screens.base.BaseFragment
import ru.iqsolution.tkoonline.screens.base.IBaseView

@Suppress("DEPRECATION")
class QrCodeFragment : BaseFragment() {

    private val adminManager: AdminManager by instance()

    private lateinit var codeScanner: CodeScanner

    private var alertDialog: AlertDialog? = null

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
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
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
            if (!adminManager.isDeviceOwner) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    promptUser("Пожалуйста, разрешите установку обновлений", Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    return false
                }
            }
        }
        return true
    }

    private fun promptUser(message: String, action: String) {
        alertDialog = alert(message, "Разрешения") {
            positiveButton("Открыть") {
                startActivity(Intent(action, Uri.fromParts("package", context?.packageName, null)))
            }
        }.show()
    }

    override fun onPause() {
        scanHandler.removeCallbacks(stopRunnable)
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onDestroyView() {
        alertDialog?.dismiss()
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