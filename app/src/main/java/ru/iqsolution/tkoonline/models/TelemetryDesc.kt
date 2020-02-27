package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

@Suppress("PropertyName")
class TelemetryDesc : TelemetryConfig() {

    @SerializedName("base_distance_desc")
    val _baseDistance = "Мин расстояние для \"Движения\""

    @SerializedName("base_degree_desc")
    val _baseDegree = "Мин поворот для \"Движения\" только в \"Движении\""

    @SerializedName("min_speed_desc")
    val _minSpeed = "Мин скорость для \"Движения\" или \"Остановки\""

    @SerializedName("min_time_desc")
    val _minTime = "Мин время для определения minSpeed из отрезков"

    @SerializedName("parking_time_desc")
    val _parkingTime = "Мин время для \"Стоянки\" только в \"Остановке\""

    @SerializedName("parking_delay_desc")
    val _parkingDelay = "Мин время для \"Стоянки\" только в \"Стоянке\""

    @SerializedName("moving_delay_desc")
    val _movingDelay = "Мин время для \"Движения\" только в \"Движении\" или \"Остановке\""

    @SerializedName("location_min_delay_desc")
    val _locationMinDelay = "Мин время для сигнализации без геолокации"

    @SerializedName("location_max_delay_desc")
    val _locationMaxDelay = "Мин время для сброса базовой т. без геолокации"

    @SerializedName("location_interval_desc")
    val _locationInterval = ""

    @SerializedName("timer_interval_desc")
    val _timerInterval = "Цикл работы таймера телеметрии"
}