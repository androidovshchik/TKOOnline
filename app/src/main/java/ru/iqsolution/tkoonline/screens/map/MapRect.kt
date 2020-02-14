package ru.iqsolution.tkoonline.screens.map

import ru.iqsolution.tkoonline.local.entities.Platform

class MapRect {

    var minLat = Double.MAX_VALUE

    var maxLat = Double.MIN_VALUE

    var minLon = Double.MAX_VALUE

    var maxLon = Double.MIN_VALUE

    val centerLat: Double
        get() = (maxLat + minLat) / 2

    val centerLon: Double
        get() = (maxLon + minLon) / 2

    fun update(platform: Platform) {
        if (platform.latitude < minLat) {
            minLat = platform.latitude
        } else if (platform.latitude > maxLat) {
            maxLat = platform.latitude
        }
        if (platform.longitude < minLon) {
            minLon = platform.longitude
        } else if (platform.longitude > maxLon) {
            maxLon = platform.longitude
        }
    }
}