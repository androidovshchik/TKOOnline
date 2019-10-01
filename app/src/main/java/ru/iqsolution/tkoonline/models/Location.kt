package ru.iqsolution.tkoonline.models

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

interface Location {

    var latitude: Double

    var longitude: Double

    /**
     * Haversine formula
     */
    fun getDistance(l: Location): Double {
        val a =
            0.5 - cos((l.latitude - latitude) * D) / 2 + cos(latitude * D) * cos(l.latitude * D) * (1 - cos((l.longitude - longitude) * D)) / 2
        return R * asin(sqrt(a))
    }

    companion object {

        private const val D = 0.017453292519943295 // Math.PI / 180

        private const val R = 12742000 // 2 * R; R = 6371 km
    }
}