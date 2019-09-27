package ru.iqsolution.tkoonline.screens.status

import android.location.Location

interface StatusListener {

    fun onLocationResult(location: Location)

    fun onLocationAvailability(available: Boolean)
}