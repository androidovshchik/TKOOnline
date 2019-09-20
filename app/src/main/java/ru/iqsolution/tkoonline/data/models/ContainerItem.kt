package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig

class ContainerItem : Container(), Comparable<ContainerItem> {

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

    override fun compareTo(other: ContainerItem): Int {
        if (status == other.status) {
            return 0
        }
        return when (status) {
            ContainerStatus.PENDING -> 1
            ContainerStatus.NOT_VISITED -> {
                when (other.status) {
                    ContainerStatus.PENDING -> -1
                    else -> 1
                }
            }
            else -> {
                when (other.status) {
                    ContainerStatus.PENDING, ContainerStatus.NOT_VISITED -> -1
                    else -> 0
                }
            }
        }
    }
}