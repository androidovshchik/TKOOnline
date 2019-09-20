package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

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

    val containerReqular = Container(ContainerType.REGULAR)

    val containerBunker = Container(ContainerType.BUNKER)

    val containerWithout = Container(ContainerType.WITHOUT)

    val containerSpecial = Container(ContainerType.SPECIAL)

    /**
     * Represents time without date [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @SerializedName("time_limit_from")
    lateinit var timeLimitFrom: DateTime

    /**
     * Represents time without date [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @SerializedName("time_limit_to")
    lateinit var timeLimitTo: DateTime

    @SerializedName("status")
    lateinit var status: ContainerStatus
}