package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import ru.iqsolution.tkoonline.Pattern
import ru.iqsolution.tkoonline.defaultZone
import ru.iqsolution.tkoonline.midnightZone
import java.io.Serializable
import java.time.ZonedDateTime

@Entity(
    tableName = "photo_events",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["pe_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["pe_token_id"])
    ]
)
class PhotoEvent() : Serializable, SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pe_id")
    override var id: Long? = null

    @ColumnInfo(name = "pe_token_id")
    override var tokenId = 0L

    @ColumnInfo(name = "pe_route_id")
    var routeId: String? = null

    @ColumnInfo(name = "pe_task_uid")
    var taskUid = 0L

    @ColumnInfo(name = "pe_task_id")
    var taskId: Int? = null

    @ColumnInfo(name = "pe_event_id")
    var eventId: Int? = null

    @ColumnInfo(name = "pe_type_id")
    var typeId = -1

    @NonNull
    @ColumnInfo(name = "pe_path")
    lateinit var path: String

    @ColumnInfo(name = "pe_latitude")
    var latitude = 0.0

    @ColumnInfo(name = "pe_longitude")
    var longitude = 0.0

    @NonNull
    @Pattern(Pattern.DATETIME_ZONE)
    @ColumnInfo(name = "pe_when_time")
    var whenTime: ZonedDateTime = ZonedDateTime.now()
        set(value) {
            field = value.withZoneSameInstant(midnightZone)
        }
        get() = field.withZoneSameInstant(defaultZone)

    @ColumnInfo(name = "pe_sent")
    override var sent = false

    constructor(task: Task, typeId: Int) : this() {
        tokenId = task.tokenId
        routeId = task.routeId
        taskUid = task.uid ?: 0L
        taskId = task.id
        this.typeId = typeId
        // required for initialization only
        path = ""
    }
}