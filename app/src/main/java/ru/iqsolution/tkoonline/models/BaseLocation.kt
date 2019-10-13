package ru.iqsolution.tkoonline.models

import android.location.Location
import android.util.SparseIntArray
import org.joda.time.DateTime
import kotlin.math.roundToInt

// todo thread safe?
/**
 * Базовая точка + базовое направление
 * 10 km/h = 2,77778 м/s
 * При такой скорости:
 * - за 30 секунд пройдет 83,3334 метра
 * - 200 метров пройдет за 72 секунды
 */
@Suppress("unused")
class BaseLocation : SimpleLocation {

    /**
     * Направление движения в градусах от направления на север
     */
    var direction: Int? = null

    /**
     * In meters
     * After 200 meters this should be replaced
     */
    var distance = 0

    private val speedMap = SparseIntArray()

    /**
     * @return time (seconds) + speed (km/h)
     */
    val speed: Pair<Int, Int>
        get() {
            val now = DateTime.now()
            val seconds = (now.millis - locationTime.withZone(now.zone).millis) / 1000
            if (seconds <= 0) {
                return 0 to 0
            }
            return seconds.toInt() to (distance / seconds * 3.6).roundToInt()
        }

    fun updateFrom(newLocation: SimpleLocation, lastLocation: SimpleLocation = this): Boolean {
        speedMap.apply {
            for (index in 0 until size()) {
                action(keyAt(index), valueAt(index))
            }
        }
        val result = FloatArray(2)
        lastLocation?.let {
            // getting only distance
            Location.distanceBetween(it.latitude, it.longitude, newLocation.latitude, newLocation.longitude, result)
        }
        // getting only angle
        direction?.let {

        }
        Location.distanceBetween(latitude, longitude, newLocation.latitude, newLocation.longitude, result)
    }

    constructor(lat: Double, lon: Double) : super(lat, lon)

    constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())

    constructor(location: Location) : super(location)
}