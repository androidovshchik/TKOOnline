@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startService
import ru.iqsolution.tkoonline.receivers.ToastReceiver

val Context.statusBarHeight: Int
    get() {
        val id = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (id > 0) {
            return resources.getDimensionPixelSize(id)
        }
        return 0
    }

fun Context.bgToast(message: String) = sendBroadcast(intentFor<ToastReceiver>().apply {
    putExtra(ToastReceiver.EXTRA_MESSAGE, message)
    putExtra(ToastReceiver.EXTRA_DURATION, Toast.LENGTH_SHORT)
})

fun Context.longBgToast(message: String) = sendBroadcast(intentFor<ToastReceiver>().apply {
    putExtra(ToastReceiver.EXTRA_MESSAGE, message)
    putExtra(ToastReceiver.EXTRA_DURATION, Toast.LENGTH_LONG)
})

tailrec fun Context?.getActivity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.getActivity()
}

inline fun <reified T> Context.doActivityCallback(action: T.() -> Unit) {
    getActivity()?.let {
        if (it is T && !it.isFinishing) {
            action(it)
        }
    }
}

fun Context.areGranted(vararg permissions: String): Boolean {
    return permissions.all { isGranted(it) }
}

fun Context.isGranted(permission: String): Boolean {
    return checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

inline fun <reified T : Service> Context.startForegroundService(vararg params: Pair<String, Any?>): ComponentName? {
    return if (isOreoPlus()) {
        startForegroundService(intentFor<T>(*params))
    } else {
        startService<T>(*params)
    }
}

@Suppress("DEPRECATION")
fun Context.scanFiles(vararg paths: String) {
    paths.forEach {
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = Uri.parse("file://$it")
        })
    }
}

fun Context.pendingFor(
    cls: Class<*>,
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
): PendingIntent =
    PendingIntent.getActivity(applicationContext, requestCode, Intent(applicationContext, cls), flags)

inline fun <reified T : Activity> Context.pendingActivityFor(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getActivity(applicationContext, requestCode, intentFor<T>(*params), flags)

inline fun <reified T : BroadcastReceiver> Context.pendingReceiverFor(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, requestCode, intentFor<T>(*params), flags)

fun Context.pendingReceiverFor(
    action: String,
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, requestCode, Intent(action), flags)