package ru.iqsolution.tkoonline.screens.base

import com.google.android.gms.location.LocationSettingsStates

interface LocationHandler {

    fun checkLocation()

    /**
     * @param state null if location settings are not satisfied
     */
    fun onLocationState(state: LocationSettingsStates?)
}