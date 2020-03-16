package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import org.jetbrains.anko.locationManager
import ru.iqsolution.tkoonline.telemetry.TelemetryListener
import java.lang.ref.WeakReference

@SuppressLint("MissingPermission")
@Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")
class LocationManager(context: Context, listener: TelemetryListener) : android.location.LocationListener {

    private val reference = WeakReference(listener)

    private val locationClient = context.locationManager

    private var satellitesCount = 0

    init {
        satellitesCount = locationClient.getGpsStatus(null)
            ?.satellites?.map { it.usedInFix() }?.size ?: 0
        listener.apply {
            onLocationAvailability(locationClient.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                onLocationChanged(it, satellitesCount)
            }
        }
    }

    fun requestUpdates(interval: Long) {
        locationClient.also {
            it.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 0f, this)
            it.registerGnssStatusCallback(gnssCallback)
        }
    }

    /**
     * NOTICE UI thread
     */
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
        if (LocationManager.GPS_PROVIDER == provider) {
            reference.get()?.onLocationAvailability(
                when (status) {
                    LocationProvider.OUT_OF_SERVICE -> false
                    else -> true
                }
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        reference.get()?.onLocationChanged(location, satellitesCount)
    }

    override fun onProviderEnabled(provider: String) {
        if (LocationManager.GPS_PROVIDER == provider) {
            reference.get()?.onLocationStart(true)
        }
    }

    override fun onProviderDisabled(provider: String) {
        if (LocationManager.GPS_PROVIDER == provider) {
            reference.get()?.onLocationStop(true)
        }
    }

    fun removeUpdates() {
        locationClient.also {
            it.unregisterGnssStatusCallback(gnssCallback)
            it.removeUpdates(this)
        }
    }

    private val gnssCallback = object : GnssStatus.Callback() {

        /**
         * NOTICE UI thread
         */
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            satellitesCount = status.satelliteCount
        }

        override fun onStarted() {
            reference.get()?.onLocationStart(false)
        }

        override fun onFirstFix(ttffMillis: Int) {
            reference.get()?.onLocationStart(false, ttffMillis)
        }

        override fun onStopped() {
            reference.get()?.onLocationStop(false)
        }
    }
}
