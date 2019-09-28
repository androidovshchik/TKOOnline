package ru.iqsolution.tkoonline.services

import android.location.Location
import com.google.android.gms.location.LocationSettingsStates

interface LocationListener {

    /**
     * @param state null if location settings are not satisfied
     */
    fun onLocationState(state: LocationSettingsStates?)

    fun onLocationAvailability(available: Boolean)

    fun onLocationResult(location: Location)
}