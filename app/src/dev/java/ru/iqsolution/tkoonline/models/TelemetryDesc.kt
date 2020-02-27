package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

class TelemetryDesc : TelemetryConfig() {

    @SerializedName("desc")
    val desc = listOf(
        "В телеметрии таймер и геолокация работают асинхронно"
    )

    @SerializedName("base_distance_desc")
    val baseDistanceDesc = "Мин расстояние для \"Движения\""

    @SerializedName("base_degree_desc")
    val baseDegreeDesc = "Мин поворот для \"Движения\" только в \"Движении\""

    @SerializedName("min_speed_desc")
    val minSpeedDesc = "Мин скорость для \"Движения\" или \"Остановки\""

    @SerializedName("min_time_desc")
    val minTimeDesc = "Мин время для min_speed из отрезков location_interval"

    @SerializedName("parking_time_desc")
    val parkingTimeDesc = "Мин время для \"Стоянки\" только в \"Остановке\""

    @SerializedName("parking_delay_desc")
    val parkingDelayDesc = "Мин время для \"Стоянки\" только в \"Стоянке\""

    @SerializedName("moving_delay_desc")
    val movingDelayDesc = "Мин время для \"Движения\" только в \"Движении\" или \"Остановке\""

    @SerializedName("location_min_delay_desc")
    val locationMinDelayDesc = "Мин время для сигнализации без геолокации"

    @SerializedName("location_max_delay_desc")
    val locationMaxDelayDesc = "Мин время для сброса базовой т. без геолокации"

    @SerializedName("location_interval_desc")
    val locationIntervalDesc = "Мин время для обновления геолокации"

    @SerializedName("timer_interval_desc")
    val timerIntervalDesc = "Мин время для таймера телеметрии"
}