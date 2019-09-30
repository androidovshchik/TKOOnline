package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import com.google.gson.annotations.SerializedName
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

    @ColumnInfo(name = "le_sent")
    override var sent = false

    /**
     * Duplicates access token value
     */
    @Ignore
    @SerializedName("auth_key")
    var authKey: String? = null

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

        @ColumnInfo(name = "le_height")
        @SerializedName("height")
        var height = 0

        @ColumnInfo(name = "le_validity")
        @SerializedName("valid")
        var validity = 0

        @ColumnInfo(name = "le_satellites")
        @SerializedName("sat_cnt")
        var satellites = 0

        @ColumnInfo(name = "le_speed")
        @SerializedName("spd")
        var speed = 0

        @ColumnInfo(name = "le_direction")
        @SerializedName("dir")
        var direction = 0

        /**
         * In meters
         */
        @ColumnInfo(name = "le_mileage")
        @SerializedName("race")
        var mileage = 0
    }
}