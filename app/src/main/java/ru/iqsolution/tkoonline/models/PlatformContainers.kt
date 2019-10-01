package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import kotlin.math.absoluteValue
import kotlin.math.cos

/**
 * Special class for non-linked platforms
 * NOTICE the subcontainers also include the primary [containerVolume] and [containerCount] values
 */
@Suppress("MemberVisibilityCanBePrivate")
class PlatformContainers() : Platform() {

    @SerializedName("_r")
    var regular = SimpleContainer()

    @SerializedName("_br")
    var bunker = SimpleContainer()

    @SerializedName("_b")
    var bunk = SimpleContainer()

    @SerializedName("_s")
    var special = SimpleContainer()

    @SerializedName("_u")
    var unknown = SimpleContainer()

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
        addContainer(platform)
    }

    /**
     * It may be important to keep the original [containerVolume] and [containerCount] values
     */
    override fun addContainer(container: Container?) {
        if (container is Platform?) {
            addContainer(container)
        } else {
            throw IllegalAccessException("Should not be called as is")
        }
    }

    fun addContainer(platform: Platform?) {
        platform?.let {
            when (it.toContainerType()) {
                ContainerType.REGULAR -> regular.addContainer(it)
                ContainerType.BUNKER -> bunker.addContainer(it)
                ContainerType.BULK1, ContainerType.BULK2 -> bunk.addContainer(it)
                ContainerType.SPECIAL1, ContainerType.SPECIAL2 -> special.addContainer(it)
                else -> unknown.addContainer(it)
            }
        }
    }

    fun addError(error: String) {
        if (!errors.contains(error)) {
            errors.add(error)
        }
    }

    /**
     * Simplifying here calculation for better performance
     */
    override fun getDistance(l: Location): Double {
        return when (status) {
            PlatformStatus.PENDING, PlatformStatus.NOT_VISITED -> super.getDistance(l)
            else -> {
                val diffLat = (latitude - l.latitude).absoluteValue * LAT1
                val diffLon = (longitude - l.longitude).absoluteValue * getLon1(l.latitude)
                return if (diffLat < RANGE_DISTANCE && diffLon < RANGE_DISTANCE) {
                    super.getDistance(l)
                } else {
                    DEFAULT_DISTANCE
                }
            }
        }
    }

    companion object {

        /**
         * Min range for better calculation
         */
        private const val RANGE_DISTANCE = 999.0

        /**
         * It's value doesn't matter but must be more than [RANGE_DISTANCE]
         */
        private const val DEFAULT_DISTANCE = 9999.0

        /**
         * Length in meters of 1° of latitude = always 111.32 km
         */
        private const val LAT1 = 111320

        /**
         * Length in meters of 1° of longitude = 40075 km * cos(latitude) / 360
         */
        private fun getLon1(latitude: Double): Double {
            return 40075000 * cos(latitude) / 360
        }
    }
}