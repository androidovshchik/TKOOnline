package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import org.joda.time.DateTime

@Entity(
    tableName = "location_events",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["le_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["le_token_id"])
    ]
)
class LocationEvent : SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "le_id")
    var id: Long? = null

    @ColumnInfo(name = "le_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "le_package_id")
    var packageId = 0

    @ColumnInfo(name = "le_event_time")
    lateinit var eventTime: DateTime

    @ColumnInfo(name = "le_latitude")
    var latitude = 0.0

    @ColumnInfo(name = "le_longitude")
    var longitude = 0.0

    @ColumnInfo(name = "le_height")
    var height = 0

    @ColumnInfo(name = "le_coords_time")
    lateinit var coordsTime: DateTime

    @ColumnInfo(name = "le_validity")
    var validity = false

    @ColumnInfo(name = "le_satellites")
    var satellites = 0

    @ColumnInfo(name = "le_speed")
    var speed = 0

    @ColumnInfo(name = "le_direction")
    var direction = 0

    @ColumnInfo(name = "le_mileage")
    var mileage = 0

    @ColumnInfo(name = "le_sent")
    override var sent = false
}
