package ru.iqsolution.tkoonline.models

/**
 * Базовая точки + базовое направление
 */
@Suppress("unused")
class BaseLocation : SimpleLocation {

    constructor(lat: Double, lon: Double) : super(lat, lon)

    constructor(lat: Float, lon: Float) : this(lat.toDouble(), lon.toDouble())

    constructor(location: android.location.Location) : super(location)
}