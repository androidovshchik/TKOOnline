package ru.iqsolution.tkoonline.screens.map

import ru.iqsolution.tkoonline.models.Location
import kotlin.math.PI
import kotlin.math.cos

class MapRect {

    var minLat = Double.MAX_VALUE

    var maxLat = Double.MIN_VALUE

    var minLon = Double.MAX_VALUE

    var maxLon = Double.MIN_VALUE

    private var updateCount = 0

    val isValid: Boolean
        get() = updateCount >= 1

    val centerLat: Double
        get() = (maxLat + minLat) / 2

    val centerLon: Double
        get() = (maxLon + minLon) / 2

    fun update(location: Location<Double>) {
        update(location.latitude, location.longitude)
    }

    fun update(latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            if (latitude < minLat) {
                minLat = latitude
            } else if (latitude > maxLat) {
                maxLat = latitude
            }
            if (longitude < minLon) {
                minLon = longitude
            } else if (longitude > maxLon) {
                maxLon = longitude
            }
            updateCount++
        }
    }

    /**
     * Length in meters of 1° of latitude = always 111.32 km
     * Length in meters of 1° of longitude = 111.32 km * cos(latitude)
     */
    fun measure() {
        if (isValid) {
            val minLength = 2 //km
            val latLength = 111.32 //km
            if (maxLat - minLat < minLength / latLength) {
                minLat -= minLength / latLength / 2
                maxLat += minLength / latLength / 2
            }
            val lonLength = latLength * cos(centerLat * PI / 180)
            if (maxLon - minLon < minLength / lonLength) {
                minLon -= minLength / lonLength / 2
                maxLon += minLength / lonLength / 2
            }
        }
    }
}