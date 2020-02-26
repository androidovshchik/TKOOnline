package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

@Suppress("PropertyName")
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
}

// Для состояния стоянка - 5 минут
private const val PARKING_DELAY = 5 * 60_000L

// Для состояния движения и остановка - 1 минута
private const val MOVING_DELAY = 60_000L