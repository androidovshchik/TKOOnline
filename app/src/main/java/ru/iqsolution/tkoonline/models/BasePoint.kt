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
class BasePoint(
    location: Location,
    private val state: TelemetryState = TelemetryState.UNKNOWN
) : SimpleLocation(location) {

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
        if (distance >= BASE_DISTANCE) {
            return TelemetryState.MOVING
        }
        getMinSpeed(MIN_TIME)?.let {
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

    private fun getMinSpeed(seconds: Int): Int? {
        val now = DateTime.now()
        val seconds = (now.millis - locationTime.withZone(now.zone).millis) / 1000
        if (seconds <= 0) {
            return 0 to 0
        }
        seconds.toInt() to (distance / seconds * 3.6).roundToInt()
    }

    companion object {

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