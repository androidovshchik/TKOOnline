package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import org.joda.time.DateTime

@Entity(
    tableName = "photo_events",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
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
class PhotoEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pe_id")
    var id: Long? = null

    @ColumnInfo(name = "pe_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "pe_type_id")
    var typeId = 0

    @ColumnInfo(name = "pe_kp_id")
    var kpId: Int? = null

    @ColumnInfo(name = "pe_path")
    lateinit var path: String

    @ColumnInfo(name = "pe_latitude")
    var latitude = 0.0

    @ColumnInfo(name = "pe_longitude")
    var longitude = 0.0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "pe_time")
    lateinit var time: DateTime

    @ColumnInfo(name = "pe_sent")
    var sent = false

    /**
     * It's needed only for platform errors
     */
    @Ignore
    var error: String? = null
}