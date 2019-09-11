package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class Datum {

    @SerializedName("kp_id")
    var kpId: Int? = null

    @SerializedName("linked_kp_id")
    var linkedKpId: Any? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("latitude")
    var latitude: Float? = null

    @SerializedName("longitude")
    var longitude: Float? = null

    @SerializedName("bal_keeper")
    var balKeeper: String? = null

    @SerializedName("bal_keeper_phone")
    var balKeeperPhone: String? = null

    @SerializedName("reg_operator")
    var regOperator: String? = null

    @SerializedName("reg_operator_phone")
    var regOperatorPhone: String? = null

    @SerializedName("container_type")
    var containerType: String? = null

    @SerializedName("container_type_volume")
    var containerTypeVolume: Int? = null

    @SerializedName("container_count")
    var containerCount: Int? = null

    @SerializedName("time_limit_from")
    var timeLimitFrom: String? = null

    @SerializedName("time_limit_to")
    var timeLimitTo: String? = null

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("linksed_kp_id")
    var linksedKpId: Int? = null
}