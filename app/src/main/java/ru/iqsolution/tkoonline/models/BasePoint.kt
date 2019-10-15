package ru.iqsolution.tkoonline.models

import android.location.Location
import android.util.SparseIntArray
import org.joda.time.DateTime
import org.joda.time.Duration
import timber.log.Timber
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
    var currentDirection = 0f

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
        Timber.i(";;;;;;;;;;;;;;;;;;;;;;")
        Timber.i(state.toString())
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
            Location.distanceBetween(latitude, longitude, location.latitude, location.longitude, output)
            val angle = if (output[1] < 0) 360 + output[1] else output[1]
            baseDirection?.let {
                currentDirection = angle
            } ?: run {
                baseDirection = angle
                currentDirection = angle
            }
        } else {
            baseDirection = null
            currentDirection = 0f
        }
        val allSeconds = Duration(
            locationTime,
            location.locationTime.withZone(locationTime.zone)
        ).standardSeconds.absoluteValue
        val lastSeconds = Duration(
            lastLocation.locationTime,
            location.locationTime.withZone(lastLocation.locationTime.zone)
        ).standardSeconds.absoluteValue
        if (allSeconds > 0) {
            speedMap.put(
                allSeconds.toInt(), if (lastSeconds > 0) {
                    (MS2KMH * space / lastSeconds).roundToInt()
                } else 0
            )
        }
        Timber.i("distance $distance")
        Timber.i("space $space")
        Timber.i("lastSeconds $lastSeconds")
        Timber.i(speedMap.toString())
        lastLocation = location
        return space
    }

    /**
     * Should be called after [updateLocation]
     */
    fun replaceWith(): TelemetryState? {
        if (distance >= BASE_DISTANCE) {
            return TelemetryState.MOVING
        }
        getMinSpeed(MIN_TIME)?.let {
            Timber.i("Min speed is $it")
            if (state != TelemetryState.MOVING) {
                if (it > MIN_SPEED) {
                    return TelemetryState.MOVING
                }
            }
            // parking cannot be replaced with stopping
            if (state != TelemetryState.STOPPING && state != TelemetryState.PARKING) {
                if (it < MIN_SPEED) {
                    return TelemetryState.STOPPING
                }
            }
        }
        when (state) {
            TelemetryState.MOVING -> {
                baseDirection?.let {
                    if ((currentDirection - it).absoluteValue >= BASE_DEGREE) {
                        if (lastSpeed >= MIN_SPEED) {
                            return TelemetryState.MOVING
                        }
                    }
                }
            }
            TelemetryState.STOPPING -> {
                val now = DateTime.now()
                if (now.millis - locationTime.withZone(now.zone).millis >= PARKING_TIME) {
                    return TelemetryState.PARKING
                }
            }
            else -> {
            }
        }
        return null
    }

    private fun getMinSpeed(limit: Int): Int? {
        var minSpeed: Int? = null
        speedMap.apply {
            if (size() > 0) {
                val lastIndex = size() - 1
                val maxSeconds = keyAt(lastIndex)
                if (maxSeconds < limit) {
                    return null
                }
                for (i in lastIndex downTo 0) {
                    if (keyAt(i) in (maxSeconds - limit)..maxSeconds) {
                        minSpeed = minSpeed?.let {
                            min(it, valueAt(i))
                        } ?: valueAt(i)
                    } else {
                        break
                    }
                }
            }
        }
        return minSpeed
    }

    override fun toString(): String {
        return "BasePoint(" +
                "state=$state, " +
                "lastLocation=${if (this == lastLocation) "this" else lastLocation}, " +
                "baseDirection=$baseDirection, " +
                "currentDirection=$currentDirection, " +
                "speedMap=$speedMap, " +
                "distance=$distance" +
                ")" +
                " ${super.toString()}"
    }

    companion object {

        private const val MS2KMH = 3.6f

        /**
         * Событие стоянка
         * Данное событие генерируется в состоянии остановка если данное состояние не изменено в течение 2 минут
         */
        private const val PARKING_TIME = 2 * 60_000L

        // Направление движения отклоняется от базового на величину 5 градусов
        private const val BASE_DEGREE = 5

        // Скорость выше параметра минимальной скорости (10км/ч)
        private const val MIN_SPEED = 10

        // минимальное время - 30 секунд
        private const val MIN_TIME = 30

        /**
         * Событие пройдена дистанция
         * Данное событие генерируется только в состоянии движения при перемещении автомобиля от базовой точки на расстояние больше 200 метров.
         */
        private const val BASE_DISTANCE = 200
    }
}