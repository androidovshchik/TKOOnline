package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig
import java.io.Serializable

open class Platform : Serializable, Container, Location<Double> {

    @SerializedName("kp_id")
    var kpId = 0

    @SerializedName("linked_kp_id")
    var linkedKpId: Int? = null

    @SerializedName("address")
    lateinit var address: String

    @SerializedName("latitude")
    override var latitude = 0.0

    @SerializedName("longitude")
    override var longitude = 0.0

    @SerializedName("bal_keeper")
    var balKeeper: String? = null

    @SerializedName("bal_keeper_phone")
    var balKeeperPhone: String? = null

    @SerializedName("reg_operator")
    var regOperator: String? = null

    @SerializedName("reg_operator_phone")
    var regOperatorPhone: String? = null

    /**
     * This value may be unknown for [ContainerType] but it's important to present the original value
     */
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
    var status = 0

    val isValid: Boolean
        get() = BuildConfig.DEBUG || status != PlatformStatus.NO_TASK.id

    fun toPlatformStatus() = PlatformStatus.fromId(status)
}