package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

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
    var errors: String? = null

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
}