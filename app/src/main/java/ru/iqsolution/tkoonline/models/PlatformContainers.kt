package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.Platform
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * Special class for displayed in list platforms
 */
@Suppress("MemberVisibilityCanBePrivate")
class PlatformContainers() : Platform() {

    /**
     * Doesn't include this [kpId]
     * if linked id == null then they are with linked id == this kp id or empty
     * if linked id != null then they are with linked id == this linked id or empty
     */
    val linkedIds = hashSetOf<Int>()

    /**
     * It's needed only for map
     */
    @SerializedName("_e")
    val errors = mutableListOf<String>()

    /**
     * It's needed for sorting primary items and sizing ovals in list
     * More than 80 meters
     */
    var meters = 99.0

    /**
     * It's needed for sorting secondary items in list
     */
    var timestamp = 0L

    var highlighted = false

    constructor(platform: Platform) : this() {
        kpId = platform.kpId
        linkedKpId = platform.linkedKpId
        address = platform.address
        latitude = platform.latitude
        longitude = platform.longitude
        balKeeper = platform.balKeeper
        balKeeperPhone = platform.balKeeperPhone
        regOperator = platform.regOperator
        regOperatorPhone = platform.regOperatorPhone
        containerType = platform.containerType
        containerVolume = platform.containerVolume
        containerCount = platform.containerCount
        timeLimitTo = platform.timeLimitTo
        timeLimitFrom = platform.timeLimitFrom
        status = platform.status
    }

    fun addError(error: String, position: Int = errors.size) {
        if (!errors.contains(error)) {
            errors.add(position, error)
        }
    }

    /**
     * Spherical law of cosinuses
     * angle = acrcos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(dlon))
     * distance = radius * angle
     */
    fun setDistanceTo(l: Location<Double>) {
        val lat1 = latitude * PI / 180
        val lat2 = l.latitude * PI / 180
        val dLon = (l.longitude - longitude) * PI / 180
        meters = 6378137 * acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(dLon))
    }

    override fun toString(): String {
        return "PlatformContainers(" +
            "linkedIds=$linkedIds, " +
            "errors=$errors, " +
            ") ${super.toString()}"
    }
}