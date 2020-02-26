package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

@Suppress("PropertyName")
class TelemetryDesc : TelemetryConfig() {

    @SerializedName("base_distance_desc")
    val _baseDistance = ""

    @SerializedName("base_degree_desc")
    val _baseDegree = ""

    @SerializedName("min_speed_desc")
    val _minSpeed = "Минимальная скорость для перехода в \"Движение\""

    @SerializedName("min_time_desc")
    val _minTime = "Минимальное время для определения minSpeed из отрезков"

    @SerializedName("parking_time_desc")
    val _parkingTime = "Время для перехода в \"Стоянку\" только в состоянии \"Остановка\""
}

// Для состояния стоянка - 5 минут
private const val PARKING_DELAY = 5 * 60_000L

// Для состояния движения и остановка - 1 минута
private const val MOVING_DELAY = 60_000L