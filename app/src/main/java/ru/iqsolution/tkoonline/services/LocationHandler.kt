package ru.iqsolution.tkoonline.services

import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import timber.log.Timber
import java.lang.ref.WeakReference

class LocationHandler(service: TelemetryService) : LocationCallback() {

    private val reference = WeakReference(service)

    override fun onLocationAvailability(availability: LocationAvailability) {
        Timber.d("onLocationAvailability $availability")
        reference.get()?.onLocationAvailability(availability.isLocationAvailable) ?: Timber.w("Reference is null")
    }

    override fun onLocationResult(result: LocationResult?) {
        Timber.d("onLocationResult $result")
        result?.lastLocation?.let {
            reference.get()?.onLocationResult(it) ?: Timber.w("Reference is null")
        } ?: Timber.w("Last location is null")
    }
}