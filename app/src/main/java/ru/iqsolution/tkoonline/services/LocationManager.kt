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
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * МП должно исключать генерацию более одного события в одну секунду.
 * Данные события должны генерироваться по факту прохождения дистанции в 200 метров, повороте, остановке или начале движения,
 * а также по времени не реже чем:
 * · Для состояния стоянка - 5 минут
 * · Для состояния движения и остановка - 1 минута
 */
@Suppress("MemberVisibilityCanBePrivate")
class LocationManager(context: Context, listener: LocationListener) : android.location.LocationListener {

    private val reference = WeakReference(listener)

    private val locationClient = context.locationManager

    private var timer: ScheduledFuture<*>? = null

    @Volatile
    private var lastLocation: Location? = null

    @Volatile
    private var availability = false

    @Volatile
    private var satellites = 0

    @SuppressLint("MissingPermission")
    fun requestUpdates() {
        locationClient.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, 0f, this)
        locationClient.registerGnssStatusCallback(gnssCallback)
        val executor = Executors.newScheduledThreadPool(1)
        timer = executor.scheduleAtFixedRate({
            reference.get()?.apply {
                onLocationAvailability(locationClient.isProviderEnabled(LocationManager.GPS_PROVIDER))
                val location = lastLocation ?: locationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    onLocationResult(SimpleLocation(location))
                }
            }
        }, 0, 5, TimeUnit.SECONDS)
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
        Timber.d("onStatusChanged $provider $status")
        if (LocationManager.GPS_PROVIDER != provider) {
            return
        }
        if () {

        }
        availability = when (status) {
            LocationProvider.OUT_OF_SERVICE -> false
            else -> true
        }
    }

    override fun onProviderEnabled(provider: String) {
        Timber.d("onProviderEnabled $provider")
    }

    override fun onProviderDisabled(provider: String) {
        Timber.d("onProviderDisabled $provider")
    }

    @SuppressLint("MissingPermission")
    fun removeUpdates() {
        timer?.cancel(true)
        locationClient.unregisterGnssStatusCallback(gnssCallback)
        locationClient.removeUpdates(this)
    }

    private val gnssCallback = object : GnssStatus.Callback() {

        override fun onSatelliteStatusChanged(status: GnssStatus) {
            satellites = status.satelliteCount
        }
    }
}
