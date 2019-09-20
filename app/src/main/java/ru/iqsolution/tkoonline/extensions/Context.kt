@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PermissionResult
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startService

fun Context.getVectorBitmap(id: Int, scale: Float = 1f): Bitmap? {
    return ContextCompat.getDrawable(applicationContext, id)?.let {
        val bitmap = Bitmap.createBitmap(
            (it.intrinsicWidth * scale).toInt(),
            (it.intrinsicHeight * scale).toInt(),
            Bitmap.Config.ARGB_8888
        )
        Canvas(bitmap).apply {
            it.setBounds(0, 0, width, height)
            it.draw(this)
        }
        return bitmap
    }
}

@PermissionResult
fun Context.areGranted(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

inline fun <reified T : Service> Context.startForegroundService(vararg params: Pair<String, Any?>): ComponentName? {
    return if (isOreoPlus()) {
        startForegroundService(intentFor<T>(*params))
    } else {
        startService<T>(*params)
    }
}

inline fun <reified T : Activity> Context.pendingActivityFor(
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getActivity(applicationContext, 0, intentFor<T>(*params), PendingIntent.FLAG_UPDATE_CURRENT)

inline fun <reified T : BroadcastReceiver> Context.pendingReceiverFor(
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, 0, intentFor<T>(*params), PendingIntent.FLAG_UPDATE_CURRENT)

fun Context.pendingReceiverFor(intent: Intent): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

inline fun <reified T : BroadcastReceiver> Context.createAlarm(interval: Int) {
    cancelAlarm<T>()
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pendingReceiverFor<T>())
}

inline fun <reified T : BroadcastReceiver> Context.cancelAlarm() {
    alarmManager.cancel(pendingReceiverFor<T>())
}