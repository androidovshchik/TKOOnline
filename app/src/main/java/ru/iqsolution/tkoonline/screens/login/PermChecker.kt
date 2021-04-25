package ru.iqsolution.tkoonline.screens.login

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.telecom.TelecomManager
import org.jetbrains.anko.telecomManager
import ru.iqsolution.tkoonline.extensions.isOreoPlus
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class PermChecker(context: Fragment) {

    private val reference = WeakReference(context)

    fun requestPerms() = reference.get()?.run {
        if (!context.areGranted(*DANGER_PERMISSIONS)) {
            DANGER_PERMISSIONS.forEach {
                if (shouldShowRequestPermissionRationale(it)) {
                    promptUser("Пожалуйста, предоставьте все разрешения", Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    return false
                }
            }
            requestPermissions(DANGER_PERMISSIONS, REQUEST_PERMS)
            return false
        }
    }

    fun requestDoze() = reference.get()?.run {
        if (!context.powerManager.isIgnoringBatteryOptimizations(packageName)) {
            // NOTICE this violates Google Play policy
            startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.fromParts("package", packageName, null)
            })
            return false
        }
    }

    fun requestCall() = reference.get()?.run {
        if (context.telecomManager.defaultDialerPackage != packageName) {
            // RoleManager is not working for some reasons
            startActivity(Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            })
            return false
        }
    }

    fun requestInstall() = reference.get()?.run {
        if (isOreoPlus()) {
            if (!adminManager.isDeviceOwner) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    promptUser("Пожалуйста, разрешите установку обновлений", Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    return false
                }
            }
        }
    }

    private fun promptUser(message: String, action: String) = reference.get()?.run {
        alertDialog = context?.alert(message, "Разрешения") {
            positiveButton("Открыть") { _, _ ->
                startActivity(Intent(action, Uri.fromParts("package", context.packageName, null)))
            }
        }?.display()
    }

    fun onRequestPermissionsResult(requestCode: Int, results: IntArray) {
        when (requestCode) {
            REQUEST_PERMS -> {
                if (results.any { it != PackageManager.PERMISSION_GRANTED }) {
                    requestPerms()
                } else {
                    requestDoze()
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            REQUEST_DOZE -> {
                if (resultCode != Activity.RESULT_OK) {
                    requestDoze()
                } else {
                    requestCall()
                }
            }
            REQUEST_CALL -> {
                if (resultCode != Activity.RESULT_OK) {
                    requestCall()
                } else {
                    requestInstall()
                }
            }
            REQUEST_INSTALL -> {
                if (resultCode != Activity.RESULT_OK) {
                    requestInstall()
                }
            }
        }
    }

    companion object {

        private const val REQUEST_DOZE = 1
        private const val REQUEST_CALL = 2
        private const val REQUEST_INSTALL = 3

        private const val REQUEST_PERMS = 1000

        private val DANGER_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }
}