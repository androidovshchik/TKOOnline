package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.models.PlatformContainers
import kotlin.math.absoluteValue

class ComparatorLocation : Comparator<PlatformContainers> {

    private var lat = 0.0

    private var lon = 0.0

    fun updateLocation(latitude: Double, longitude: Double) {
        lat = latitude
        lon = longitude
    }

    override fun compare(a: PlatformContainers, b: PlatformContainers): Int {
        val diffA = (lat - a.latitude).absoluteValue + (lon - a.longitude).absoluteValue
        val diffB = (lat - b.latitude).absoluteValue + (lon - b.longitude).absoluteValue
        return if (diffA < diffB) 1 else -1
    }
}