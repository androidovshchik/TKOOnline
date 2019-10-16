package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.models.BasePoint
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

    @Embedded
    @SerializedName("data")
    lateinit var data: Data

    @ColumnInfo(name = "le_state")
    lateinit var state: String

    @ColumnInfo(name = "le_wait")
    var waiting = false

    @ColumnInfo(name = "le_sent")
    override var sent = false

    /**
     * Duplicates access token value
     */
    @Ignore
    @SerializedName("auth_key")
    var authKey: String? = null

    constructor(
        basePoint: BasePoint,
        token: Long,
        pckg: Int,
        distance: Int
    ) : this() {
        tokenId = token
        packageId = pckg
        data = Data().apply {
            // time is correct here
            whenTime = DateTime.now()
            basePoint.lastLocation.let {
                locationTime = it.locationTime
                latitude = it.latitude
                longitude = it.longitude
                altitude = it.altitude
                validity = it.validity
                satellites = it.satellites
            }
            speed = basePoint.lastSpeed
            direction = basePoint.currentDirection?.roundToInt()
            mileage = distance
        }
    }

    class Data {

        /**
         * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
         */
        @ColumnInfo(name = "le_when_time")
        @SerializedName("event_time")
        lateinit var whenTime: DateTime

        /**
         * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
         */
        @ColumnInfo(name = "le_location_time")
        @SerializedName("time")
        lateinit var locationTime: DateTime

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
                val now = DateTime.now()
                if (now.millis - locationTime.withZone(now.zone).millis <= VALID_TIME) {
                    return 1
                }
                return 0
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

    companion object {

        private const val VALID_TIME = 5000L
    }
}