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

    var lastLocation: SimpleLocation = this

    /**
     * Направление движения в градусах от направления на север
     */
    private var baseDirection: Int? = null

    var currentDirection: Int? = null

    /**
     * It's not a session mileage, it's a distance between this base point and current location
     * After 200 meters this should be replaced
     */
    private var distance = 0f

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

    fun updateFrom(newLocation: SimpleLocation): Float {
        val result = FloatArray(2)
        // getting only distance
        Location.distanceBetween(
            lastLocation.latitude,
            lastLocation.longitude,
            newLocation.latitude,
            newLocation.longitude,
            result
        )
        val space = result[0]
        distance += space
        // getting only angle
        direction?.let {

        } ?: run {
            direction =
        }
        Location.distanceBetween(latitude, longitude, newLocation.latitude, newLocation.longitude, result)
        speedMap.apply {
            for (index in 0 until size()) {
                action(keyAt(index), valueAt(index))
            }
        }
        lastLocation = newLocation
        return space
    }

    fun shouldBeReplaced(state: TelemetryState): Boolean {
        when (state) {
            TelemetryState.UNKNOWN -> {
            }
            TelemetryState.MOVING -> {
            }
            TelemetryState.STOPPING -> {
            }
            TelemetryState.PARKING -> {
            }
        }
    }

    constructor(lat: Double, lon: Double) : super(lat, lon)

    constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())

    constructor(location: Location) : super(location)
}