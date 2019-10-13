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
class BasePoint : SimpleLocation {

    private val state: TelemetryState

    var lastLocation: SimpleLocation = this

    /**
     * Направление движения в градусах от направления на север
     */
    private var baseDirection: Int? = null

    var currentDirection: Int? = null

    /**
     * It's not a session mileage, it's a distance between this (as base) and current location
     */
    private var distance = 0f

    private val speedMap = SparseIntArray()

    constructor(lat: Double, lon: Double) : super(lat, lon)

    constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())

    constructor(location: Location) : super(location)

    constructor(location: SimpleLocation) : super(location)

    constructor(state: TelemetryState, location: SimpleLocation) : super(location)

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

    fun replaceWith(state: TelemetryState): TelemetryState? {
        if (distance >= 200) {
            return TelemetryState.MOVING
        }
        when (state) {
            TelemetryState.UNKNOWN -> {
                if (isExpired(TelemetryState.MOVING)) {
                    return isExpired(TelemetryState.STOPPING)
                }
            }
            TelemetryState.MOVING -> {
            }
            TelemetryState.STOPPING -> {
                if (locationTime.millis >= 2 * 60_000L) {
                    return true
                }
            }
            TelemetryState.PARKING -> {
            }
        }
        return null
    }
}