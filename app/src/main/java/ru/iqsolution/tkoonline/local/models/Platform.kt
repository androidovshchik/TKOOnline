package ru.iqsolution.tkoonline.local.models

import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.PlatformStatus

@Entity(tableName = "platforms")
class Platform {

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

    @Ignore
    @SerializedName("container_type")
    lateinit var containerType: ContainerType

    @Ignore
    @SerializedName("container_type_volume")
    var containerVolume = 0f

    @Ignore
    @SerializedName("container_count")
    var containerCount = 0

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


    val isEmpty: Boolean
        get() = containerVolume < 0.1f

    constructor(type: ContainerType) : this() {
        containerType = type
    }

    fun addFrom(container: ru.iqsolution.tkoonline.local.Container) {
        if (isEmpty) {
            containerVolume = container.containerVolume
        }
        containerCount += container.containerCount
    }

    val isValid: Boolean
        get() = containerType != ContainerType.UNKNOWN && (BuildConfig.DEBUG || status != PlatformStatus.NO_TASK)
}