package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.Pattern
import ru.iqsolution.tkoonline.defaultZone
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.Location
import ru.iqsolution.tkoonline.models.PlatformStatus
import java.io.Serializable
import java.time.LocalDate
import java.time.OffsetTime

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["tk_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tk_token_id"])
    ]
)
class Task : Serializable, Container, Location<Double> {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tk_uid")
    var uid: Long? = null

    @ColumnInfo(name = "tk_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "tk_route_id")
    var routeId: String? = null

    @SerializedName("id")
    @ColumnInfo(name = "tk_id")
    var id: Int? = null

    @SerializedName("cs_id")
    @ColumnInfo(name = "tk_kp_id")
    var kpId: Int? = null

    @SerializedName("task_type")
    @ColumnInfo(name = "tk_type_id")
    var typeId = 0

    @NonNull
    @SerializedName("address")
    @ColumnInfo(name = "tk_address")
    lateinit var address: String

    @SerializedName("latitude")
    @ColumnInfo(name = "tk_lat")
    override var latitude = 0.0

    @SerializedName("longitude")
    @ColumnInfo(name = "tk_lon")
    override var longitude = 0.0

    @SerializedName("bal_keeper")
    @ColumnInfo(name = "tk_bal_keeper")
    var balKeeper: String? = null

    @SerializedName("bal_keeper_phone")
    @ColumnInfo(name = "tk_keeper_phone")
    var balKeeperPhone: String? = null

    @SerializedName("reg_operator")
    @ColumnInfo(name = "tk_reg_operator")
    var regOperator: String? = null

    @SerializedName("reg_operator_phone")
    @ColumnInfo(name = "tk_operator_phone")
    var regOperatorPhone: String? = null

    @NonNull
    @SerializedName("container_type")
    @ColumnInfo(name = "tk_container_type")
    override lateinit var containerType: String

    @SerializedName("container_volume")
    @ColumnInfo(name = "tk_container_volume")
    override var containerVolume = 0f

    @SerializedName("container_count")
    @ColumnInfo(name = "tk_container_count")
    override var containerCount = 0

    @NonNull
    @Pattern(Pattern.TIME_ZONE)
    @SerializedName("time_limit_from")
    @ColumnInfo(name = "tk_time_from")
    var timeLimitFrom: OffsetTime = OffsetTime.now()
        get() = field.withOffsetSameInstant(defaultZone)

    @NonNull
    @Pattern(Pattern.TIME_ZONE)
    @SerializedName("time_limit_to")
    @ColumnInfo(name = "tk_time_to")
    var timeLimitTo: OffsetTime = OffsetTime.now()
        get() = field.withOffsetSameInstant(defaultZone)

    @SerializedName("status")
    @ColumnInfo(name = "tk_status")
    var status = PlatformStatus.NO_TASK.id

    @SerializedName("too_late")
    @ColumnInfo(name = "tk_late")
    var tooLate = false

    @ColumnInfo(name = "tk_is_draft")
    var draft = false

    @NonNull
    @ColumnInfo(name = "tk_when_day")
    lateinit var whenDay: LocalDate

    fun toPlatformStatus() = PlatformStatus.fromId(status)
}