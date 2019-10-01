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
        var R = 6371; // Radius of the earth in km
        var dLat = deg2rad(lat2 - lat1);  // deg2rad below
        var dLon = deg2rad(lon2 - lon1);
        var a =
            sin(dLat / 2) * Math.sin(dLat / 2) +
                    cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
        ;
        var c = 2 * atan2(sqrt(a), sqrt(1 - a));
        var d = R * c; // Distance in km
        return d;
    }

    fun deg2rad(deg) {
        return deg * (Math.PI / 180)
    }

    companion object {

        private const val D = 0.017453292519943295 // Math.PI / 180

        private const val R = 12742000 // 2 * R; R = 6371 km
    }
}