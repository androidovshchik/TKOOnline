package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * Special class for non-linked platforms
 * NOTICE containers also include platform [containerVolume] and [containerCount] values
 */
@Suppress("MemberVisibilityCanBePrivate")
class PlatformContainers() : Platform() {

    var containers = arrayListOf(
        SimpleContainer(ContainerType.REGULAR),
        SimpleContainer(ContainerType.BUNKER),
        SimpleContainer(ContainerType.BULK1),
        SimpleContainer(ContainerType.SPECIAL1),
        SimpleContainer(ContainerType.UNKNOWN)
    )

    /**
     * It's needed only for map
     */
    @SerializedName("_e")
    var errors = arrayListOf<String>()

    /**
     * It's needed for sorting primary items and sizing ovals in list
     */
    var meters = DEFAULT_DISTANCE

    /**
     * It's needed for sorting secondary items in list
     */
    var timestamp = 0L

    constructor(platform: Platform) : this() {
        kpId = platform.kpId
        linkedKpId = platform.kpId
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
        setFromAny(platform)
    }

    val allLinkedIds: List<Int>
        get() {
            val all = arrayListOf<Int>()
            containers.forEach {
                all.addAll(it.linkedIds)
            }
            return all
        }

    override fun setFromEqual(container: Container?) {
        if (container is Platform?) {
            setFromEqual(container)
        } else {
            throw IllegalAccessException("Should not be called as is")
        }
    }

    fun setFromEqual(platform: Platform?) {
        platform?.let {
            containers.forEach { container ->
                container.setFromEqual(it)
            }
        }
    }

    override fun setFromAny(container: Container?) {
        if (container is Platform?) {
            setFromAny(container)
        } else {
            throw IllegalAccessException("Should not be called as is")
        }
    }

    fun setFromAny(platform: Platform?) {
        platform?.let {
            containers.forEach { container ->
                container.setFromAny(it)
            }
        }
    }

    fun addError(error: String) {
        if (!errors.contains(error)) {
            errors.add(error)
        }
    }

    /**
     * Spherical law of cosinuses
     * angle = acrcos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(dlon))
     * distance = radius * angle
     */
    fun setDistanceTo(l: Location<Double>) {
        val lat1 = latitude * D
        val lat2 = l.latitude * D
        val dLon = (l.longitude - longitude) * D
        meters = R * acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(dLon))
    }

    companion object {

        /**
         * More than 80 meters
         */
        private const val DEFAULT_DISTANCE = 99.0

        /**
         * Count of radians in one degree
         */
        private const val D = Math.PI / 180

        /**
         * Radius of the earth in meters
         */
        private const val R = 6378137
    }
}