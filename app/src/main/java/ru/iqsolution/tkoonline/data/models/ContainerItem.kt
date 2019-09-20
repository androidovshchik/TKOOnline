package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

class ContainerItem : Comparable<ContainerItem> {

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

    @SerializedName("container_type")
    lateinit var containerType: String

    @SerializedName("container_type_volume")
    var containerTypeVolume = 0f

    @SerializedName("container_count")
    var containerCount = 0

    /**
     * Represents time without date
     */
    @SerializedName("time_limit_from")
    lateinit var timeLimitFrom: DateTime

    /**
     * Represents time without date
     */
    @SerializedName("time_limit_to")
    lateinit var timeLimitTo: DateTime

    @SerializedName("status")
    lateinit var status: ContainerStatus

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