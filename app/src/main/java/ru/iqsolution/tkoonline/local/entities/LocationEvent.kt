package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.extensions.Pattern
import ru.iqsolution.tkoonline.extensions.isEarlier
import ru.iqsolution.tkoonline.models.BasePoint
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

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
class LocationEvent() : SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "le_id")
    override var id: Long? = null

    @ColumnInfo(name = "le_token_id")
    override var tokenId = 0L

    @ColumnInfo(name = "le_package_id")
    @SerializedName("id")
    var packageId = 0

    @ColumnInfo(name = "le_version")
    @SerializedName("v")
    var version = 1

    @NonNull
    @Embedded
    @SerializedName("data")
    lateinit var data: Data

    @NonNull
    @ColumnInfo(name = "le_state")
    @SerializedName("_s")
    lateinit var state: String

    @ColumnInfo(name = "le_wait")
    @SerializedName("_w")
    var waiting = false

    @ColumnInfo(name = "le_sent")
    @SerializedName("_t")
    override var sent = false

    val isValid: Boolean
        get() = !data.whenTime.isEarlier(2, TimeUnit.DAYS)

    constructor(basePoint: BasePoint, token: Long, pckg: Int, distance: Float, wait: Boolean = false) : this() {
        tokenId = token
        packageId = pckg
        // debug info
        state = basePoint.state.name
        waiting = wait
        data = Data().apply {
            // time is correct here
            whenTime = ZonedDateTime.now()
            basePoint.lastLocation.let {
                locationTime = it.locationTime
                latitude = it.latitude
                longitude = it.longitude
                altitude = it.altitude
                validity = it.validity
                satellites = it.satellites
            }
            speed = basePoint.lastSpeed
            direction = basePoint.currentDirection?.roundToInt() ?: -1
            mileage = distance.roundToInt()
        }
    }

    class Data {

        @NonNull
        @Pattern(Pattern.DATETIME_ZONE)
        @ColumnInfo(name = "le_when_time")
        @SerializedName("event_time")
        lateinit var whenTime: ZonedDateTime

        @NonNull
        @Pattern(Pattern.DATETIME_ZONE)
        @ColumnInfo(name = "le_location_time")
        @SerializedName("time")
        lateinit var locationTime: ZonedDateTime

        @ColumnInfo(name = "le_latitude")
        @SerializedName("lat")
        var latitude = 0.0

        @ColumnInfo(name = "le_longitude")
        @SerializedName("lon")
        var longitude = 0.0

        @ColumnInfo(name = "le_altitude")
        @SerializedName("height")
        var altitude = 0

        /**
         * признак валидности координат 1-валидные 0 - не валидные
         * Не валидные считаются координаты полученные более 5 секунд назад или с погрешностью более 30 метров
         */
        @ColumnInfo(name = "le_validity")
        @SerializedName("valid")
        var validity: Int = 0
            get() {
                if (field == 0) {
                    return 0
                }
                return if (locationTime.isEarlier(5, TimeUnit.SECONDS)) 0 else 1
            }

        @ColumnInfo(name = "le_satellites")
        @SerializedName("sat_cnt")
        var satellites = 0

        @ColumnInfo(name = "le_speed")
        @SerializedName("spd")
        var speed = 0

        @ColumnInfo(name = "le_direction")
        @SerializedName("dir")
        var direction: Int? = null

        /**
         * In meters
         */
        @ColumnInfo(name = "le_mileage")
        @SerializedName("race")
        var mileage = 0
    }
}