package ru.iqsolution.tkoonline.telemetry

import android.content.Context
import android.os.Handler
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
            Handler().postDelayed(this, 3000)
        }
    }
}