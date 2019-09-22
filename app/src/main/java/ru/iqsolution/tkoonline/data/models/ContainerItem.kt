package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig

class ContainerItem : Container() {

    @SerializedName("kp_id")
    var kpId = 0

    @SerializedName("linked_kp_id")
    var linkedKpId: Int? = null

    @SerializedName("address")
    lateinit var address: String

    @SerializedName("latitude")
    var latitude = 0.0

    @SerializedName("longitude")
    var longitude = 0.0

    @SerializedName("bal_keeper")
    var balKeeper: String? = null

    @SerializedName("bal_keeper_phone")
    var balKeeperPhone: String? = null

    @SerializedName("reg_operator")
    var regOperator: String? = null

    @SerializedName("reg_operator_phone")
    var regOperatorPhone: String? = null

    /**
     * It will not contain [containerCount] and may be match [containerVolume] even if this has the same [containerType]
     */
    val containerRegular = Container(ContainerType.REGULAR)

    /**
     * It will not contain [containerCount] and may be match [containerVolume] even if this has the same [containerType]
     */
    val containerBunker = Container(ContainerType.BUNKER)

    /**
     * It will not contain [containerCount] and may be match [containerVolume] even if this has the same [containerType]
     */
    val containerWithout = Container(ContainerType.WITHOUT)

    /**
     * It will not contain [containerCount] and may be match [containerVolume] even if this has the same [containerType]
     */
    val containerSpecial = Container(ContainerType.SPECIAL)

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @SerializedName("time_limit_from")
    lateinit var timeLimitFrom: DateTime

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @SerializedName("time_limit_to")
    lateinit var timeLimitTo: DateTime

    @SerializedName("status")
    lateinit var status: ContainerStatus

    val isValid: Boolean
        get() = containerType != ContainerType.UNKNOWN && (BuildConfig.DEBUG || status != ContainerStatus.NO_TASK)

    companion object {

        fun copyFrom(original: ContainerItem): ContainerItem {
            return ContainerItem().apply {
                kpId = original.kpId
                linkedKpId = original.kpId
                address = original.address
                latitude = original.latitude
                longitude = original.longitude
                balKeeper = original.balKeeper
                balKeeperPhone = original.balKeeperPhone
                regOperator = original.regOperator
                regOperatorPhone = original.regOperatorPhone
                containerType = original.containerType
                containerVolume = original.containerVolume
                containerCount = original.containerCount
                containerRegular.apply {
                    containerType = original.containerRegular.containerType
                    containerVolume = original.containerRegular.containerVolume
                    containerCount = original.containerRegular.containerCount
                }
                containerBunker.apply {
                    containerType = original.containerBunker.containerType
                    containerVolume = original.containerBunker.containerVolume
                    containerCount = original.containerBunker.containerCount
                }
                containerWithout.apply {
                    containerType = original.containerWithout.containerType
                    containerVolume = original.containerWithout.containerVolume
                    containerCount = original.containerWithout.containerCount
                }
                containerSpecial.apply {
                    containerType = original.containerSpecial.containerType
                    containerVolume = original.containerSpecial.containerVolume
                    containerCount = original.containerSpecial.containerCount
                }
                timeLimitTo = original.timeLimitTo
                timeLimitFrom = original.timeLimitFrom
                status = original.status
            }
        }
    }
}