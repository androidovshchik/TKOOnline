package ru.iqsolution.tkoonline.models

import android.location.Location
import java.io.Serializable

class SimpleLocation() : Serializable {

    var latitude = 0.0

    var longitude = 0.0

    constructor(lat: Double, lon: Double) : this() {
        latitude = lat
        longitude = lon
    }

    constructor(lat: Float, lon: Float) : this() {
        latitude = lat.toDouble()
        longitude = lon.toDouble()
    }

    constructor(location: Location) : this() {
        latitude = location.latitude
        longitude = location.longitude
    }
}