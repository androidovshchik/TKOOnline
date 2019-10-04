package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import org.joda.time.DateTime
import java.io.Serializable

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
class PhotoEvent() : Serializable, SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pe_id")
    override var id: Long? = null

    @ColumnInfo(name = "pe_token_id")
    override var tokenId = 0L

    @ColumnInfo(name = "pe_kp_id")
    var kpId: Int? = null

    @ColumnInfo(name = "pe_type")
    var type = -1

    @Volatile
    @ColumnInfo(name = "pe_path")
    lateinit var path: String

    @ColumnInfo(name = "pe_latitude")
    var latitude = 0.0

    @ColumnInfo(name = "pe_longitude")
    var longitude = 0.0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "pe_when_time")
    lateinit var whenTime: DateTime

    @ColumnInfo(name = "pe_sent")
    override var sent = false

    constructor(typeId: Int) : this() {
        type = typeId
        // mock data
        path = ""
        whenTime = DateTime.now()
    }

    constructor(id: Int, typeId: Int) : this() {
        kpId = id
        type = typeId
        // mock data
        path = ""
        whenTime = DateTime.now()
    }
}