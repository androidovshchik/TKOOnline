package ru.iqsolution.tkoonline.models

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface Location {

    var latitude: Double

    var longitude: Double

    /**
     * https://stackoverflow.com/a/27943/5279156
     */
    fun getDistance(l: Location): Double {
        val dLat = (l.latitude - latitude) * D
        val dLon = (l.longitude - longitude) * D
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(latitude * D) * cos(l.latitude * D) * sin(dLon / 2) * sin(dLon / 2)
        return 2 * R * atan2(sqrt(a), sqrt(1 - a))
    }

    companion object {

        /**
         * Count of radians in one degree
         */
        const val D = Math.PI / 180

        /**
         * Radius of the earth in meters
         */
        const val R = 6371000
    }
}