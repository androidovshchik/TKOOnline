package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.Pattern
import ru.iqsolution.tkoonline.defaultZone
import ru.iqsolution.tkoonline.midnightZone
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.Location
import java.time.ZonedDateTime

@Entity(
    tableName = "task_events",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["te_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["te_token_id"])
    ]
)
class TaskEvent() : Container, Location<Double>, SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "te_id")
    override var id: Long? = null

    @ColumnInfo(name = "te_token_id")
    override var tokenId = 0L

    @ColumnInfo(name = "te_route_id")
    var routeId: String? = null

    @ColumnInfo(name = "te_task_uid")
    var taskUid: Long? = null

    @SerializedName("task_id")
    @ColumnInfo(name = "te_task_id")
    var taskId: Int? = null

    @SerializedName("task_type")
    @ColumnInfo(name = "te_type_id")
    var typeId = 0

    @SerializedName("latitude")
    @ColumnInfo(name = "te_lat")
    override var latitude = 0.0

    @SerializedName("longitude")
    @ColumnInfo(name = "te_lon")
    override var longitude = 0.0

    @NonNull
    @SerializedName("container_type_fact")
    @ColumnInfo(name = "te_container_type")
    override lateinit var containerType: String

    @SerializedName("container_type_volume_fact")
    @ColumnInfo(name = "te_container_volume")
    override var containerVolume = 0f

    @SerializedName("container_count_fact")
    @ColumnInfo(name = "te_container_count")
    override var containerCount = 0

    @NonNull
    @Pattern(Pattern.DATETIME_ZONE)
    @SerializedName("time")
    @ColumnInfo(name = "te_when_time")
    var whenTime: ZonedDateTime = ZonedDateTime.now()
        set(value) {
            field = value.withZoneSameInstant(midnightZone)
        }
        get() = field.withZoneSameInstant(defaultZone)

    @ColumnInfo(name = "te_sent")
    override var sent = false

    constructor(task: Task) : this() {
        tokenId = task.tokenId
        routeId = task.routeId
        taskUid = task.uid
        taskId = task.id
        typeId = task.typeId
        // required for initialization only
        containerType = ContainerType.UNKNOWN.id
    }
}