package ru.iqsolution.tkoonline.models

@Suppress("unused")
enum class TelemetryState {
    UNKNOWN,
    MOVING/*Движение*/,
    STOPPING/*Остановка*/,
    PARKING/*Стоянка*/;
}