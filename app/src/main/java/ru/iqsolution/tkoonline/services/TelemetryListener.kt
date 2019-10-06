package ru.iqsolution.tkoonline.services

import ru.iqsolution.tkoonline.models.SimpleLocation

interface TelemetryListener {

    fun startTelemetry()

    fun updateTelemetry(location: SimpleLocation)

    fun stopTelemetry()
}