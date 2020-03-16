package ru.iqsolution.tkoonline.telemetry

import com.google.android.gms.location.LocationSettingsStates
import ru.iqsolution.tkoonline.models.SimpleLocation

interface LocationListener {

    /**
     * May be called only from activity
     * @param state null if location settings are not satisfied
     */
    fun onLocationState(state: LocationSettingsStates?)

    fun onLocationAvailability(available: Boolean)

    fun onLocationResult(location: SimpleLocation)
}