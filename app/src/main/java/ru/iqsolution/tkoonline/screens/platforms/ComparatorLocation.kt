package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.models.PlatformContainers
import kotlin.math.absoluteValue

class ComparatorLocation : Comparator<PlatformContainers> {

    var lat = 0.0

    var lon = 0.0

    override fun compare(a: PlatformContainers, b: PlatformContainers): Int {
        val diffA = (lat - a.latitude).absoluteValue + (lon - a.longitude).absoluteValue
        val diffB = (lat - b.latitude).absoluteValue + (lon - b.longitude).absoluteValue
        return if (diffA < diffB) 1 else -1
    }
}