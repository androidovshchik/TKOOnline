package ru.iqsolution.tkoonline.services

import android.location.Location

interface LocationListener {

    fun onLocationResult(location: Location)

    fun onLocationAvailability(available: Boolean)
}