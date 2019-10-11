package ru.iqsolution.tkoonline.services

import android.location.Location

interface TelemetryListener : LocationListener {

    fun onLocationChanged(location: Location, satellites: Int)
}