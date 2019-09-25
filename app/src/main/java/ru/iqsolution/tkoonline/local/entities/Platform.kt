package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.PlatformStatus

@Entity(
    tableName = "platforms",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["p_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["p_kp_id"], unique = true),
        Index(value = ["p_token_id"])
    ]
)
class Platform : Container {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "p_id")
    var id: Long? = null

    @ColumnInfo(name = "p_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "p_kp_id")
    @SerializedName("kp_id")
    var kpId = 0

    @ColumnInfo(name = "p_linked_id")
    @SerializedName("linked_kp_id")
    var linkedKpId: Int? = null

    @ColumnInfo(name = "p_address")
    @SerializedName("address")
    lateinit var address: String

    @ColumnInfo(name = "p_latitude")
    @SerializedName("latitude")
    var latitude = 0.0

    @ColumnInfo(name = "p_longitude")
    @SerializedName("longitude")
    var longitude = 0.0

    @ColumnInfo(name = "p_bal_keeper")
    @SerializedName("bal_keeper")
    var balKeeper: String? = null

    @ColumnInfo(name = "p_bal_keeper_phone")
    @SerializedName("bal_keeper_phone")
    var balKeeperPhone: String? = null

    @ColumnInfo(name = "p_reg_operator")
    @SerializedName("reg_operator")
    var regOperator: String? = null

    @ColumnInfo(name = "p_reg_operator_phone")
    @SerializedName("reg_operator_phone")
    var regOperatorPhone: String? = null

    @ColumnInfo(name = "p_container_type")
    @SerializedName("container_type")
    override lateinit var containerType: String

    @ColumnInfo(name = "p_container_volume")
    @SerializedName("container_type_volume")
    override var containerVolume = 0f

    @ColumnInfo(name = "p_container_count")
    @SerializedName("container_count")
    override var containerCount = 0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @ColumnInfo(name = "p_time_from")
    @SerializedName("time_limit_from")
    lateinit var timeLimitFrom: DateTime

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @ColumnInfo(name = "p_time_to")
    @SerializedName("time_limit_to")
    lateinit var timeLimitTo: DateTime

    @ColumnInfo(name = "p_status")
    @SerializedName("status")
    lateinit var status: PlatformStatus

    val isValid: Boolean
        get() = BuildConfig.DEBUG || status != PlatformStatus.NO_TASK
}