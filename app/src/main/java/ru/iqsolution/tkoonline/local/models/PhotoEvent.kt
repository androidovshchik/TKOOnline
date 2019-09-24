package ru.iqsolution.tkoonline.local.models

import androidx.room.*
import org.joda.time.DateTime

@Entity(
    tableName = "photo_events",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["p_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class PhotoEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "p_id")
    var id: Long? = null

    @ColumnInfo(name = "p_token_id", index = true)
    var tokenId = 0L

    @ColumnInfo(name = "p_kp_id")
    var kpId: Int? = null

    @ColumnInfo(name = "p_type_id")
    var typeId = 0

    @ColumnInfo(name = "p_path")
    lateinit var path: String

    @ColumnInfo(name = "p_latitude")
    var latitude = 0.0

    @ColumnInfo(name = "p_longitude")
    var longitude = 0.0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "p_time")
    lateinit var time: DateTime

    @Embedded
    var token: Token? = null
}