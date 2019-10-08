@file:Suppress("unused", "DEPRECATION")

package ru.iqsolution.tkoonline.extensions

import android.app.ActivityManager
import android.app.Service

inline fun <reified T : Service> ActivityManager.isRunning(): Boolean {
    for (service in getRunningServices(Integer.MAX_VALUE)) {
        if (T::class.java.name == service.service.className) {
            return true
        }
    }
    return false
}

fun ActivityManager.getActivities(packageName: String): Int {
    for (task in getRunningTasks(Integer.MAX_VALUE)) {
        if (task.baseActivity?.packageName?.startsWith(packageName) == true) {
            return task.numActivities
        }
    }
    return 0
}