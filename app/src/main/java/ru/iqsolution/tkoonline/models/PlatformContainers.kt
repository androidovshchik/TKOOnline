package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

/**
 * Special class for non-linked platforms
 * NOTICE the subcontainers include the original [containerVolume] and [containerCount] values
 */
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
    }

    /**
     * NOTICE
     */
    override fun addContainer(container: Container?) {

    }
}