package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig

open class Platform : Container {

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
    override lateinit var containerType: String

    @SerializedName("container_type_volume")
    override var containerVolume = 0f

    @SerializedName("container_count")
    override var containerCount = 0

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
    lateinit var status: PlatformStatus

    val isValid: Boolean
        get() = BuildConfig.DEBUG || status != PlatformStatus.NO_TASK
}