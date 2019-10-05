package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.models.Location.Companion.D
import ru.iqsolution.tkoonline.models.Location.Companion.R
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

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

    val allLinkedIds: List<Int>
        get() = regular.linkedIds + bunker.linkedIds + bunk.linkedIds + special.linkedIds + unknown.linkedIds

    override val isEmpty: Boolean
        get() = regular.isEmpty && bunker.isEmpty && bunk.isEmpty && special.isEmpty && unknown.isEmpty

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
        val dLat = (l.latitude - latitude) * D
        val dLon = (l.longitude - longitude) * D
        // todo
        /*when (status) {
            PlatformStatus.PENDING, PlatformStatus.NOT_VISITED -> {
            }
            else -> {
                if (dLat * LAT1 in -RANGE_DISTANCE..RANGE_DISTANCE || dLon * getLon1(l.latitude) in -RANGE_DISTANCE..RANGE_DISTANCE) {
                    return DEFAULT_DISTANCE
                }
            }
        }*/
        // https://stackoverflow.com/a/21623206/5279156
        val a = 0.5 - cos(dLat) / 2 + cos(latitude * D) * cos(l.latitude * D) * (1 - cos(dLon)) / 2
        return 2 * R * asin(sqrt(a))
    }

    companion object {

        /**
         * Min range for better calculation
         */
        private const val RANGE_DISTANCE = 9999.0

        /**
         * It's value doesn't matter but must be more than [RANGE_DISTANCE]
         */
        private const val DEFAULT_DISTANCE = 99999.0

        /**
         * Length in meters of 1° of latitude = always 111.32 km
         */
        private const val LAT1 = 111320

        /**
         * Length in meters of 1° of longitude = 40075 km * cos(latitude) / 360
         */
        private fun getLon1(latitude: Double): Double {
            return 40075000 * cos(latitude * D) / 360
        }
    }
}