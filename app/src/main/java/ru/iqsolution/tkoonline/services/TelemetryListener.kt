package ru.iqsolution.tkoonline.services

import android.location.Location

interface TelemetryListener : LocationListener {

    fun onLocationChanged(location: Location, satellitesCount: Int)

    fun startTelemetry()

    fun stopTelemetry()
}