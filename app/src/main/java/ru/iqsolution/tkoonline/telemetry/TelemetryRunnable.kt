package ru.iqsolution.tkoonline.telemetry

import android.content.Context
import android.os.Handler
import android.os.Looper
import timber.log.Timber
import java.lang.ref.WeakReference

class TelemetryRunnable(context: Context) : Runnable {

    private val reference = WeakReference(context)

    override fun run() {
        try {
            reference.get()?.let {
                TelemetryService.start(it)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            Handler(Looper.getMainLooper()).postDelayed(this, 3000)
        }
    }
}