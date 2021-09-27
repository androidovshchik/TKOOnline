package ru.iqsolution.tkoonline.models

import android.location.Location
import android.util.SparseIntArray
import ru.iqsolution.tkoonline.extensions.isEarlier
import timber.log.Timber
import java.time.Duration
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Базовая точка + базовое направление
 * 10 km/h = 2,77778 м/s
 * При такой скорости:
 * - за 30 секунд пройдет 83,3334 метра
 * - 200 метров пройдет за 72 секунды
 */
class BasePoint(
    location: SimpleLocation,
    val state: TelemetryState = TelemetryState.UNKNOWN
) : SimpleLocation(location.latitude, location.longitude, location.locationTime) {

    /**
     * Last known location
     */
    var lastLocation: SimpleLocation = this

    /**
     * Направление движения в градусах от направления на север
     */
    private var baseDirection: Float? = null

    /**
     * Will be the same as [baseDirection] on first value
     */
    var currentDirection: Float? = null

    /**
     * Keys are duration in seconds, values are speed in km/h
     */
    private val speedMap = SparseIntArray()

    val lastSpeed: Int
        get() = speedMap.run {
            if (size() > 0) {
                valueAt(size() - 1)
            } else 0
        }

    /**
     * It's not a session mileage, it's a distance between this (as base) and [lastLocation] (as current)
     */
    private var distance = 0f

    init {
        altitude = location.altitude
        satellites = location.satellites
        accuracy = location.accuracy
    }

    /**
     * Shouldn't be called on class init and in [TelemetryState.PARKING] state
     * @return distance traveled in meters
     */
    fun updateLocation(location: SimpleLocation): Float {
        val output = FloatArray(2)
        // getting only distance
        Location.distanceBetween(
            lastLocation.latitude,
            lastLocation.longitude,
            location.latitude,
            location.longitude,
            output
        )
        val space = output[0]
        distance += space
        if (state != TelemetryState.PARKING) {
            // getting only angle
            Location.distanceBetween(
                latitude,
                longitude,
                location.latitude,
                location.longitude,
                output
            )
            val angle = if (output[1] < 0) 360 + output[1] else output[1]
            baseDirection?.let {
                currentDirection = angle
            } ?: run {
                baseDirection = angle
                currentDirection = angle
            }
        } else {
            baseDirection = null
            currentDirection = null
        }
        val seconds = abs(Duration.between(locationTime, location.locationTime).toSeconds())
        val millis = abs(Duration.between(lastLocation.locationTime, location.locationTime).toMillis())
        if (seconds > 0) {
            speedMap.put(
                seconds.toInt(), if (millis > 0) {
                    (M_MS2KM_H * space / millis).roundToInt()
                } else 0
            )
        }
        lastLocation = location
        Timber.i(
            """
            $state
            Base dir $baseDirection current dir $currentDirection
            Distance $distance
            Space $space
            Millis $millis
            $speedMap
        """.trimIndent()
        )
        return space
    }

    /**
     * Should be called after [updateLocation]
     */
    fun replaceWith(config: TelemetryConfig): TelemetryState? {
        if (distance >= config.baseDistance) {
            return TelemetryState.MOVING
        }
        getMinSpeed(config.minTime)?.let {
            Timber.i("Min speed is $it")
            if (state != TelemetryState.MOVING) {
                if (it > config.minSpeed) {
                    return TelemetryState.MOVING
                }
            }
            // parking cannot be replaced with stopping
            if (state != TelemetryState.STOPPING && state != TelemetryState.PARKING) {
                if (it < config.minSpeed) {
                    return TelemetryState.STOPPING
                }
            }
        }
        when (state) {
            TelemetryState.MOVING -> {
                baseDirection?.let { b ->
                    currentDirection?.let { c ->
                        if ((c - b).absoluteValue >= config.baseDegree) {
                            if (lastSpeed >= config.minSpeed) {
                                return TelemetryState.MOVING
                            }
                        }
                    }
                }
            }
            TelemetryState.STOPPING -> {
                if (locationTime.isEarlier(config.parkingTime)) {
                    return TelemetryState.PARKING
                }
            }
            else -> {
            }
        }
        return null
    }

    @Suppress("SameParameterValue")
    private fun getMinSpeed(interval: Int): Int? = speedMap.run {
        var minSpeed: Int? = null
        if (size() > 0) {
            val lastIndex = size() - 1
            val maxSeconds = keyAt(lastIndex)
            if (interval > maxSeconds) {
                return null
            }
            for (i in lastIndex downTo 0) {
                if (keyAt(i) in (maxSeconds - interval)..maxSeconds) {
                    minSpeed = minSpeed?.let {
                        min(it, valueAt(i))
                    } ?: valueAt(i)
                } else {
                    removeAt(i)
                }
            }
        }
        return minSpeed
    }

    companion object {

        private const val M_MS2KM_H = 3600f
    }
}