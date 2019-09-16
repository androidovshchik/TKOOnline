package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import org.joda.time.LocalTime

class ContainerInfo {

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

    @SerializedName("time_limit_from")
    lateinit var timeLimitFrom: LocalTime

    @SerializedName("time_limit_to")
    lateinit var timeLimitTo: LocalTime

    @SerializedName("status")
    var status = 0
}