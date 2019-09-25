package ru.iqsolution.tkoonline.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.PlatformStatus

@Entity(
    tableName = "platforms",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["pf_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class Platform : Container {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pf_id")
    var id: Long? = null

    @ColumnInfo(name = "pf_token_id", index = true)
    var tokenId = 0L

    @ColumnInfo(name = "pf_id")
    @SerializedName("kp_id")
    var kpId = 0

    @ColumnInfo(name = "pf_id")
    @SerializedName("linked_kp_id")
    var linkedKpId: Int? = null

    @ColumnInfo(name = "pf_id")
    @SerializedName("address")
    lateinit var address: String

    @ColumnInfo(name = "pf_id")
    @SerializedName("latitude")
    var latitude = 0.0

    @ColumnInfo(name = "pf_id")
    @SerializedName("longitude")
    var longitude = 0.0

    @ColumnInfo(name = "pf_id")
    @SerializedName("bal_keeper")
    var balKeeper: String? = null

    @ColumnInfo(name = "pf_id")
    @SerializedName("bal_keeper_phone")
    var balKeeperPhone: String? = null

    @ColumnInfo(name = "pf_id")
    @SerializedName("reg_operator")
    var regOperator: String? = null

    @ColumnInfo(name = "pf_id")
    @SerializedName("reg_operator_phone")
    var regOperatorPhone: String? = null

    @ColumnInfo(name = "pf_id")
    @SerializedName("container_type")
    override lateinit var containerType: ContainerType

    @ColumnInfo(name = "pf_id")
    @SerializedName("container_type_volume")
    override var containerVolume = 0f

    @ColumnInfo(name = "pf_id")
    @SerializedName("container_count")
    override var containerCount = 0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @ColumnInfo(name = "pf_id")
    @SerializedName("time_limit_from")
    lateinit var timeLimitFrom: DateTime

    /**
     * [ru.iqsolution.tkoonline.PATTERN_TIME]
     */
    @ColumnInfo(name = "pf_id")
    @SerializedName("time_limit_to")
    lateinit var timeLimitTo: DateTime

    @ColumnInfo(name = "pf_id")
    @SerializedName("status")
    lateinit var status: PlatformStatus

    val isValid: Boolean
        get() = containerType != ContainerType.UNKNOWN && (BuildConfig.DEBUG || status != PlatformStatus.NO_TASK)
}