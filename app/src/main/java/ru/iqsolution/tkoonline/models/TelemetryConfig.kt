package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

@Suppress("PropertyName")
class TelemetryConfig {

    /**
     * Событие пройдена дистанция
     * Данное событие генерируется только в состоянии движения при перемещении автомобиля от базовой точки на расстояние больше 200 метров.
     */
    @SerializedName("base_distance.meters")
    var baseDistance = 200

    @SerializedName("base_distance_desc")
    val _baseDistance = ""

    // Направление движения отклоняется от базового на величину 5 градусов
    @SerializedName("base_degree.degrees")
    var baseDegree = 5

    @SerializedName("base_degree_desc")
    val _baseDegree = ""

    // Скорость выше параметра минимальной скорости (10км/ч)
    @SerializedName("min_speed.km_h")
    var minSpeed = 10

    @SerializedName("min_speed_desc")
    val _minSpeed = ""

    // минимальное время - 30 секунд
    @SerializedName("min_time.seconds")
    var minTime = 30

    @SerializedName("min_time_desc")
    val _minTime = ""

    /**
     * Событие стоянка
     * Данное событие генерируется в состоянии остановка если данное состояние не изменено в течение 2 минут
     */
    // in seconds
    @SerializedName("parking_time.seconds")
    var parkingTime = 2 * 60

    @SerializedName("parking_time_desc")
    val _parkingTime = ""
}

// Для состояния стоянка - 5 минут
private const val PARKING_DELAY = 5 * 60_000L

// Для состояния движения и остановка - 1 минута
private const val MOVING_DELAY = 60_000L