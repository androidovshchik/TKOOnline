package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import ru.iqsolution.tkoonline.Pattern
import ru.iqsolution.tkoonline.defaultZone
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.Location
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.patternTimeZone
import java.io.Serializable
import java.time.LocalDate
import java.time.OffsetTime

@Entity(
    tableName = "drafts",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["d_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["d_token_id"])
    ]
)
class Draft : Serializable, Container, Location<Double> {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "d_uid")
    var uid: Long? = null

    @ColumnInfo(name = "d_route_id")
    var routeId: String? = null

    @ColumnInfo(name = "d_id")
    var id: Int? = null

    @ColumnInfo(name = "d_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "d_type_id")
    var typeId = 0

    @NonNull
    @ColumnInfo(name = "d_address")
    var address = "Работа без задания"

    @ColumnInfo(name = "d_lat")
    override var latitude = 0.0

    @ColumnInfo(name = "d_lon")
    override var longitude = 0.0

    @NonNull
    @ColumnInfo(name = "d_container_type")
    override var containerType = ContainerType.REGULAR.id

    @ColumnInfo(name = "d_container_volume")
    override var containerVolume = ContainerType.REGULAR.defVolume

    @ColumnInfo(name = "d_container_count")
    override var containerCount = 1

    @NonNull
    @Pattern(Pattern.TIME_ZONE)
    @ColumnInfo(name = "d_time_from")
    var timeLimitFrom: OffsetTime = OffsetTime.parse("07:00:00+0300", patternTimeZone)
        get() = field.withOffsetSameInstant(defaultZone)

    @NonNull
    @Pattern(Pattern.TIME_ZONE)
    @ColumnInfo(name = "d_time_to")
    var timeLimitTo: OffsetTime = OffsetTime.parse("23:00:00+0300", patternTimeZone)
        get() = field.withOffsetSameInstant(defaultZone)

    @ColumnInfo(name = "d_status")
    var status = PlatformStatus.NO_TASK.id

    @NonNull
    @ColumnInfo(name = "d_day")
    lateinit var day: LocalDate

    fun toPlatformStatus() = PlatformStatus.fromId(status)
}