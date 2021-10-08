package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import ru.iqsolution.tkoonline.Pattern
import ru.iqsolution.tkoonline.defaultZone
import ru.iqsolution.tkoonline.midnightZone
import java.io.Serializable
import java.time.LocalDate
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
class PhotoEvent() : Serializable, Unique, SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pe_id")
    override var id: Long? = null

    /**
     * May be changed
     */
    @ColumnInfo(name = "pe_token_id")
    override var tokenId = 0L

    @ColumnInfo(name = "pe_route_id")
    override var routeId: String? = null

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

    @NonNull
    @ColumnInfo(name = "pe_day")
    override lateinit var whenDay: LocalDate

    @ColumnInfo(name = "pe_sent")
    override var sent = false

    constructor(tokenId: Long, routeId: String?, typeId: Int) : this() {
        this.tokenId = tokenId
        this.routeId = routeId
        this.typeId = typeId
        // required for initialization only
        path = ""
    }
}