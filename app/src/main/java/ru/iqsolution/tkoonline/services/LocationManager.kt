package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import org.jetbrains.anko.locationManager
import java.lang.ref.WeakReference

@SuppressLint("MissingPermission")
@Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")
class LocationManager(context: Context, listener: TelemetryListener) : android.location.LocationListener {

    private val reference = WeakReference(listener)

    private val locationClient = context.locationManager

    private var satellitesCount = 0

    init {
        satellitesCount = locationClient.getGpsStatus(null)
            .satellites.map { it.usedInFix() }.size
        listener.apply {
            onLocationAvailability(locationClient.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                onLocationChanged(it, satellitesCount)
            }
        }
    }

    fun requestUpdates() {
        locationClient.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, 0f, this)
        locationClient.registerGnssStatusCallback(gnssCallback)
    }

    /**
     * NOTICE UI thread
     */
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
        if (LocationManager.GPS_PROVIDER != provider) {
            return
        }
        reference.get()?.onLocationAvailability(
            when (status) {
                LocationProvider.OUT_OF_SERVICE -> false
                else -> true
            }
        )
    }

    /**
     * NOTICE UI thread
     */
    override fun onLocationChanged(location: Location) {
        reference.get()?.onLocationChanged(location, satellitesCount)
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    fun removeUpdates() {
        locationClient.unregisterGnssStatusCallback(gnssCallback)
        locationClient.removeUpdates(this)
    }

    private val gnssCallback = object : GnssStatus.Callback() {

        /**
         * NOTICE UI thread
         */
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            satellitesCount = status.satelliteCount
        }
    }

    companion object {

        private const val LOCATION_INTERVAL = 5000L
    }
}
