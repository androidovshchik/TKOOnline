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
    @SerializedName("base_distance.meters")
    val baseDistance = getOrDefault("base_distance.meters", 120).toInt()

    // Направление движения отклоняется от базового
    @SerializedName("base_degree.degrees")
    val baseDegree = getOrDefault("base_degree.degrees", 5).toInt()

    // Скорость выше параметра минимальной скорости
    @SerializedName("min_speed.km_h")
    val minSpeed = getOrDefault("min_speed.km_h", 7).toInt()

    @SerializedName("min_time.seconds")
    val minTime = getOrDefault("min_time.seconds", 30).toInt()

    /**
     * Событие стоянка
     * Данное событие генерируется в состоянии остановка если данное состояние не изменено
     */
    @SerializedName("parking_time.seconds")
    private val _parkingTime = getOrDefault("parking_time.seconds", 60).toInt()

    val parkingTime = _parkingTime * 1000L

    // Для состояния стоянка
    @SerializedName("parking_delay.seconds")
    private val _parkingDelay = getOrDefault("parking_delay.seconds", 120).toInt()

    val parkingDelay = _parkingDelay * 1000L

    // Для состояния движения и остановка
    @SerializedName("moving_delay.seconds")
    private val _movingDelay = getOrDefault("moving_delay.seconds", 10).toInt()

    val movingDelay = _movingDelay * 1000L

    /**
     * Min timeout of no input locations
     */
    @SerializedName("location_min_delay.seconds")
    private val _locationMinDelay = getOrDefault("location_min_delay.seconds", 15).toInt()

    val locationMinDelay = _locationMinDelay * 1000L

    /**
     * Max timeout of no input locations
     */
    @SerializedName("location_max_delay.seconds")
    private val _locationMaxDelay = getOrDefault("location_max_delay.seconds", 30).toInt()

    val locationMaxDelay = _locationMaxDelay * 1000L

    @SerializedName("location_interval.millis")
    val locationInterval = getOrDefault("location_interval.millis", 5000L).toLong()

    @SerializedName("timer_interval.millis")
    val timerInterval = getOrDefault("timer_interval.millis", 1500L).toLong()

    companion object {

        @Suppress("SpellCheckingInspection")
        const val DESC_CLASS = "ru.iqsolution.tkoonline.models.TelemetryDesc"

        val map: MutableMap<String, Any> = Collections.synchronizedMap(ArrayMap<String, Any>())
        
        private fun <T : Number> getOrDefault(key: String, defValue: T): Double {
            return map.getOrDefault(key, defValue.toDouble()) as Double
        }
    }
}