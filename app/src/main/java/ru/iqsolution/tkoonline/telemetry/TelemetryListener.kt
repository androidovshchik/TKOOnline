package ru.iqsolution.tkoonline.telemetry

import android.location.Location
import ru.iqsolution.tkoonline.services.LocationListener

interface TelemetryListener : LocationListener {

    fun startTelemetry()

    fun onLocationStart(enabled: Boolean, ttffMillis: Int = -1)

    fun onLocationChanged(location: Location, satellitesCount: Int)

    fun onLocationStop(disabled: Boolean)

    fun stopTelemetry()
}