package ru.iqsolution.tkoonline.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "locations")
class LocationEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "access_token")
    lateinit var accessToken: String

    @ColumnInfo(name = "package_id")
    var packageId = 0

    @ColumnInfo(name = "event_time")
    lateinit var eventTime: DateTime

    @ColumnInfo(name = "latitude")
    var latitude = 0.0

    @ColumnInfo(name = "longitude")
    var longitude = 0.0

    @ColumnInfo(name = "height")
    var height = 0

    @ColumnInfo(name = "coords_time")
    lateinit var coordsTime: DateTime

    @ColumnInfo(name = "validity")
    var validity = false

    @ColumnInfo(name = "satellites")
    var satellites = 0

    @ColumnInfo(name = "speed")
    var speed = 0

    @ColumnInfo(name = "direction")
    var direction = 0

    @ColumnInfo(name = "mileage")
    var mileage = 0
}
