package ru.iqsolution.tkoonline.models

import java.io.Serializable

class SimpleLocation() : Location, Serializable {

    override var latitude = 0.0

    override var longitude = 0.0

    constructor(lat: Double, lon: Double) : this() {
        latitude = lat
        longitude = lon
    }

    constructor(lat: Float, lon: Float) : this() {
        latitude = lat.toDouble()
        longitude = lon.toDouble()
    }

    constructor(location: android.location.Location) : this() {
        latitude = location.latitude
        longitude = location.longitude
    }
}