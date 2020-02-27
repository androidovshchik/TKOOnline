package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

open class TelemetryConfig {

    /**
     * Событие пройдена дистанция
     * Данное событие генерируется только в состоянии движения при перемещении автомобиля от базовой точки на расстояние больше 200 метров.
     */
    @SerializedName("base_distance.meters")
    val baseDistance = 200

    // Направление движения отклоняется от базового на величину 5 градусов
    @SerializedName("base_degree.degrees")
    val baseDegree = 5

    // Скорость выше параметра минимальной скорости (10км/ч)
    @SerializedName("min_speed.km_h")
    val minSpeed = 10

    // минимальное время - 30 секунд
    @SerializedName("min_time.seconds")
    val minTime = 30

    /**
     * Событие стоянка
     * Данное событие генерируется в состоянии остановка если данное состояние не изменено в течение 2 минут
     */
    // in seconds
    @SerializedName("parking_time.seconds")
    val parkingTime = 2 * 60

    // Для состояния стоянка - 5 минут (in seconds)
    @SerializedName("parking_delay.seconds")
    val parkingDelay = 5 * 60

    // Для состояния движения и остановка - 1 минута (in seconds)
    @SerializedName("moving_delay.seconds")
    val movingDelay = 60

    /**
     * Min timeout of no input locations (in seconds)
     */
    @SerializedName("location_min_delay.seconds")
    val locationMinDelay = 15

    /**
     * Max timeout of no input locations (in seconds)
     */
    @SerializedName("location_max_delay.seconds")
    val locationMaxDelay = 60

    @SerializedName("location_interval.millis")
    val locationInterval = 5000L

    @SerializedName("timer_interval.millis")
    val timerInterval = 1500L
}