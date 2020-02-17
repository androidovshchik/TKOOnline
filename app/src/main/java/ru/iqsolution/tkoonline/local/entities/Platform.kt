package ru.iqsolution.tkoonline.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.Location
import ru.iqsolution.tkoonline.models.PlatformStatus
import java.io.Serializable

@Entity(
    tableName = "platforms"
)
open class Platform : Serializable, Container, Location<Double> {

    @PrimaryKey
    @SerializedName("kp_id")
    @ColumnInfo(name = "p_kp_id")
    var kpId = 0

    @SerializedName("linked_kp_id")
    @ColumnInfo(name = "p_linked_id")
    var linkedKpId: Int? = null

    @SerializedName("address")
    @ColumnInfo(name = "p_address")
    lateinit var address: String

    @SerializedName("latitude")
    @ColumnInfo(name = "p_lat")
    override var latitude = 0.0

    @SerializedName("longitude")
    @ColumnInfo(name = "p_lon")
    override var longitude = 0.0

    @SerializedName("bal_keeper")
    @ColumnInfo(name = "p_bal_keeper")
    var balKeeper: String? = null

    @SerializedName("bal_keeper_phone")
    @ColumnInfo(name = "p_keeper_phone")
    var balKeeperPhone: String? = null

    @SerializedName("reg_operator")
    @ColumnInfo(name = "p_reg_operator")
    var regOperator: String? = null

    @SerializedName("reg_operator_phone")
    @ColumnInfo(name = "p_operator_phone")
    var regOperatorPhone: String? = null

    /**
     * This value may be unknown for [ContainerType] but it's important to present the original value
     */
    @SerializedName("container_type")
    @ColumnInfo(name = "p_container_type")
    override lateinit var containerType: String

    @SerializedName("container_volume")
    @ColumnInfo(name = "p_container_volume")
    override var containerVolume = 0f

    @SerializedName("container_count")
    @ColumnInfo(name = "p_container_count")
    override var containerCount = 0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @SerializedName("time_limit_from")
    @ColumnInfo(name = "p_time_from")
    lateinit var timeLimitFrom: DateTime

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @SerializedName("time_limit_to")
    @ColumnInfo(name = "p_time_to")
    lateinit var timeLimitTo: DateTime

    @SerializedName("status")
    @ColumnInfo(name = "p_status")
    var status = PlatformStatus.NO_TASK.id

    val isValid: Boolean
        get() = BuildConfig.DEBUG || status != PlatformStatus.NO_TASK.id

    fun toPlatformStatus() = PlatformStatus.fromId(status)

    override fun toString(): String {
        return "Platform(" +
            "kpId=$kpId, " +
            "linkedKpId=$linkedKpId, " +
            "latitude=$latitude, " +
            "longitude=$longitude, " +
            "containerType='$containerType', " +
            "containerVolume=$containerVolume, " +
            "containerCount=$containerCount, " +
            "status=$status" +
            ")"
    }
}