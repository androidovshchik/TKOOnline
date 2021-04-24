package ru.iqsolution.tkoonline.models

import androidx.collection.ArrayMap
import com.google.gson.annotations.SerializedName
import java.util.*

@Suppress("PropertyName")
open class TelemetryConfig {

    /**
     * Событие пройдена дистанция
     * Данное событие генерируется только в состоянии движения при перемещении автомобиля от базовой точки
     */
    val baseDistance: Int
        @SerializedName("base_distance.meters")
        get() = map.getOrDefault("base_distance.meters", 120) as Int

    // Направление движения отклоняется от базового
    val baseDegree: Int
        @SerializedName("base_degree.degrees")
        get() = map.getOrDefault("base_degree.degrees", 5) as Int

    // Скорость выше параметра минимальной скорости
    val minSpeed: Int
        @SerializedName("min_speed.km_h")
        get() = map.getOrDefault("min_speed.km_h", 7) as Int

    val minTime: Int
        @SerializedName("min_time.seconds")
        get() = map.getOrDefault("min_time.seconds", 30) as Int

    /**
     * Событие стоянка
     * Данное событие генерируется в состоянии остановка если данное состояние не изменено
     */
    private val _parkingTime: Int
        @SerializedName("parking_time.seconds")
        get() = map.getOrDefault("parking_time.seconds", 60) as Int

    val parkingTime: Long
        get() = _parkingTime * 1000L

    // Для состояния стоянка
    private val _parkingDelay: Int
        @SerializedName("parking_delay.seconds")
        get() = map.getOrDefault("parking_delay.seconds", 120) as Int

    val parkingDelay: Long
        get() = _parkingDelay * 1000L

    // Для состояния движения и остановка
    private val _movingDelay: Int
        @SerializedName("moving_delay.seconds")
        get() = map.getOrDefault("moving_delay.seconds", 10) as Int

    val movingDelay: Long
        get() = _movingDelay * 1000L

    /**
     * Min timeout of no input locations
     */
    private val _locationMinDelay: Int
        @SerializedName("location_min_delay.seconds")
        get() = map.getOrDefault("location_min_delay.seconds", 15) as Int

    val locationMinDelay: Long
        get() = _locationMinDelay * 1000L

    /**
     * Max timeout of no input locations
     */
    private val _locationMaxDelay: Int
        @SerializedName("location_max_delay.seconds")
        get() = map.getOrDefault("location_max_delay.seconds", 30) as Int

    val locationMaxDelay: Long
        get() = _locationMaxDelay * 1000L

    val locationInterval: Long
        @SerializedName("location_interval.millis")
        get() = map.getOrDefault("location_interval.millis", 5000L) as Long

    val timerInterval: Long
        @SerializedName("timer_interval.millis")
        get() = map.getOrDefault("timer_interval.millis", 1500L) as Long

    @Suppress("SpellCheckingInspection")
    companion object {

        const val DESC_CLASS = "ru.iqsolution.tkoonline.models.TelemetryDesc"

        val map: MutableMap<String, Any> = Collections.synchronizedMap(ArrayMap<String, Any>())
    }
}