package ru.iqsolution.tkoonline.models

import android.location.Location
import android.util.SparseIntArray
import org.joda.time.DateTime
import kotlin.math.absoluteValue
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
class BasePoint(
    location: Location,
    private val state: TelemetryState = TelemetryState.UNKNOWN
) : SimpleLocation(location) {

    var lastLocation: SimpleLocation = this

    /**
     * Направление движения в градусах от направления на север
     */
    private var baseDirection: Int? = null

    /**
     * Will be the same as [baseDirection] on first value
     */
    var currentDirection = 0

    /**
     * It's not a session mileage, it's a distance between this (as base) and [lastLocation] (as current)
     */
    private var distance = 0f

    /**
     * Max average of keys [0, 24] days as milliseconds
     */
    private val speedMap = SparseIntArray()

    /**
     * @return time (seconds) + speed (km/h)
     */
    val lastSpeed: Int
        get() = speedMap.run {
            if (size() > 0) {
                valueAt(size() - 1)
            } else 0
        }

    /**
     * @return distance traveled
     */
    fun updateFrom(location: Location): Float {
        val result = FloatArray(2)
        // getting only distance
        Location.distanceBetween(
            lastLocation.latitude,
            lastLocation.longitude,
            location.latitude,
            location.longitude,
            result
        )
        val space = result[0]
        distance += space
        // getting only angle
        direction?.let {

        } ?: run {
            direction =
        }
        Location.distanceBetween(latitude, longitude, location.latitude, location.longitude, result)
        speedMap.apply {
            for (index in 0 until size()) {
                action(keyAt(index), valueAt(index))
            }
        }
        lastLocation = newLocation
        return space
    }

    /**
     * Should be called after [updateFrom]
     */
    fun replaceWith(): TelemetryState? {
        if (distance >= 200) {
            return TelemetryState.MOVING
        }
        getMinSpeed(30)?.let {
            if (it > 10) {
                return TelemetryState.MOVING
            }
        }
        when (state) {
            TelemetryState.UNKNOWN -> {
            }
            TelemetryState.MOVING -> {
                baseDirection?.let {
                    if ((currentDirection - it).absoluteValue >= 5) {
                        return TelemetryState.MOVING
                    }
                }
            }
            TelemetryState.STOPPING -> {
                val now = DateTime.now()
                if (now.millis - locationTime.withZone(now.zone).millis >= 2 * 60_000L) {
                    return TelemetryState.PARKING
                }
            }
            TelemetryState.PARKING -> {
                // ignoring next conditions
                return null
            }
        }
        getMinSpeed(30)?.let {
            if (it < 10) {
                return TelemetryState.STOPPING
            }
        }
        return null
    }

    private fun getMinSpeed(seconds: Int): Int? {
        val now = DateTime.now()
        val seconds = (now.millis - locationTime.withZone(now.zone).millis) / 1000
        if (seconds <= 0) {
            return 0 to 0
        }
        seconds.toInt() to (distance / seconds * 3.6).roundToInt()
    }
}