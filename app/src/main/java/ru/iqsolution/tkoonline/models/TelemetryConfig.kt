package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

@Suppress("PropertyName")
open class TelemetryConfig {

    /**
     * Событие пройдена дистанция
     * Данное событие генерируется только в состоянии движения при перемещении автомобиля от базовой точки
     */
    @SerializedName("base_distance.meters")
    val baseDistance = 120

    // Направление движения отклоняется от базового
    @SerializedName("base_degree.degrees")
    val baseDegree = 5

    // Скорость выше параметра минимальной скорости
    @SerializedName("min_speed.km_h")
    val minSpeed = 7

    @SerializedName("min_time.seconds")
    val minTime = 30

    /**
     * Событие стоянка
     * Данное событие генерируется в состоянии остановка если данное состояние не изменено
     */
    @SerializedName("parking_time.seconds")
    private val _parkingTime = 60

    val parkingTime = _parkingTime * 1000L

    // Для состояния стоянка
    @SerializedName("parking_delay.seconds")
    private val _parkingDelay = 120

    val parkingDelay = _parkingDelay * 1000L

    // Для состояния движения и остановка
    @SerializedName("moving_delay.seconds")
    private val _movingDelay = 10

    val movingDelay = _movingDelay * 1000L

    /**
     * Min timeout of no input locations
     */
    @SerializedName("location_min_delay.seconds")
    private val _locationMinDelay = 15

    val locationMinDelay = _locationMinDelay * 1000L

    /**
     * Max timeout of no input locations
     */
    @SerializedName("location_max_delay.seconds")
    private val _locationMaxDelay = 30

    val locationMaxDelay = _locationMaxDelay * 1000L

    @SerializedName("location_interval.millis")
    val locationInterval = 5000L

    @SerializedName("timer_interval.millis")
    val timerInterval = 1500L
}