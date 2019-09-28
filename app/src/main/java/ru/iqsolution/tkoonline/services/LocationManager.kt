package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import ru.iqsolution.tkoonline.DANGER_PERMISSIONS
import ru.iqsolution.tkoonline.extensions.areGranted
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("MemberVisibilityCanBePrivate")
class LocationManager(context: Context, listener: LocationListener) {

    private val reference = WeakReference(listener)

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.create().apply {
        interval = 15_000L
        fastestInterval = 15_000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    fun requestUpdates(context: Context) {
        if (context.areGranted(*DANGER_PERMISSIONS)) {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    fun release() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationAvailability(availability: LocationAvailability) {
            Timber.d("onLocationAvailability $availability")
            reference.get()?.onLocationAvailability(availability.isLocationAvailable)
        }

        override fun onLocationResult(result: LocationResult?) {
            Timber.d("onLocationResult $result")
            result?.lastLocation?.let {
                Timber.i("Last location is $it")
                reference.get()?.onLocationResult(it)
            } ?: run {
                Timber.w("Last location is null")
            }
        }
    }
}
