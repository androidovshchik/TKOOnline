package ru.iqsolution.tkoonline.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.LocationManager
import org.jetbrains.anko.locationManager
import ru.iqsolution.tkoonline.models.SimpleLocation
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
class LocationManager(context: Context, listener: LocationListener) {

    private val reference = WeakReference(listener)

    private val locationClient = context.locationManager

    private var timer: ScheduledFuture<*>? = null

    @Volatile
    private var satellites = 0

    private var timerTick = 0L

    @SuppressLint("MissingPermission")
    fun requestUpdates() {
        locationClient.registerGnssStatusCallback(gnssCallback)
        val executor = Executors.newScheduledThreadPool(1)
        timer = executor.scheduleAtFixedRate({
            if (timerTick % 3 == 0L) {
                locationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {

                }
                val location = SimpleLocation(locationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER))
                reference.get()?.apply {
                    onLocationAvailability(locationClient.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    onLocationResult(location)
                }
            }
            timerTick++
        }, 0, 5, TimeUnit.SECONDS)
    }

    fun removeUpdates() {
        timer?.cancel(true)
        locationClient.unregisterGnssStatusCallback(gnssCallback)
    }

    private val gnssCallback = object : GnssStatus.Callback() {

        override fun onSatelliteStatusChanged(status: GnssStatus) {
            satellites = status.satelliteCount
        }
    }
}
