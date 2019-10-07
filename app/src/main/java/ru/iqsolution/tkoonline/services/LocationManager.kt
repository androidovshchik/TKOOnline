package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import org.jetbrains.anko.locationManager
import ru.iqsolution.tkoonline.LOCATION_INTERVAL
import ru.iqsolution.tkoonline.models.SimpleLocation
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * МП должно исключать генерацию более одного события в одну секунду.
 * Данные события должны генерироваться по факту прохождения дистанции в 200 метров, повороте, остановке или начале движения,
 * а также по времени не реже чем:
 * · Для состояния стоянка - 5 минут
 * · Для состояния движения и остановка - 1 минута
 */
@Suppress("MemberVisibilityCanBePrivate")
@SuppressLint("MissingPermission")
class LocationManager(context: Context, listener: LocationListener) : android.location.LocationListener {

    private val reference = WeakReference(listener)

    private val locationClient = context.locationManager

    @Volatile
    private var satellitesCount = 0

    init {
        listener.apply {
            onLocationAvailability(locationClient.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                onLocationResult(SimpleLocation(it).apply {
                    satellites = satellitesCount
                })
            }
        }
    }

    fun requestUpdates() {
        locationClient.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, 0f, this)
        locationClient.registerGnssStatusCallback(gnssCallback)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
        Timber.d("onStatusChanged $provider $status")
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

    override fun onLocationChanged(location: Location) {
        reference.get()?.onLocationResult(SimpleLocation(location).apply {
            satellites = satellitesCount
        })
    }

    override fun onProviderEnabled(provider: String) {
        Timber.d("onProviderEnabled $provider")
    }

    override fun onProviderDisabled(provider: String) {
        Timber.d("onProviderDisabled $provider")
    }

    fun removeUpdates() {
        locationClient.unregisterGnssStatusCallback(gnssCallback)
        locationClient.removeUpdates(this)
    }

    private val gnssCallback = object : GnssStatus.Callback() {

        override fun onSatelliteStatusChanged(status: GnssStatus) {
            satellitesCount = status.satelliteCount
        }
    }
}
