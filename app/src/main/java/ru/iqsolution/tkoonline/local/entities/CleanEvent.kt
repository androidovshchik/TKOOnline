package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType

@Entity(
    tableName = "clean_events",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["ce_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CleanEvent::class,
            parentColumns = ["ce_id"],
            childColumns = ["ce_related_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ce_token_id"]),
        Index(value = ["ce_related_id"])
    ]
)
class CleanEvent() : Container, SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ce_id")
    override var id: Long? = null

    /**
     * May be changed
     */
    @ColumnInfo(name = "ce_token_id")
    override var tokenId = 0L

    @ColumnInfo(name = "ce_kp_id")
    override var kpId = 0

    @ColumnInfo(name = "ce_linked_id")
    var linkedId: Int? = null

    /**
     * It's value is not [kpId] but it is [id] of parent platform
     */
    @ColumnInfo(name = "ce_related_id")
    var relatedId: Long? = null

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "ce_when_time")
    @SerializedName("time")
    lateinit var whenTime: DateTime

    /**
     * May be changed
     */
    @ColumnInfo(name = "ce_container_type")
    @SerializedName("container_type_fact")
    override lateinit var containerType: String

    @ColumnInfo(name = "ce_container_volume")
    @SerializedName("container_type_volume_fact")
    override var containerVolume = 0f

    @ColumnInfo(name = "ce_container_count")
    @SerializedName("container_count_fact")
    override var containerCount = 0

    @ColumnInfo(name = "ce_sent")
    override var sent = false

    constructor(kp: Int, token: Long) : this() {
        kpId = kp
        tokenId = token
        // time is correct here
        whenTime = DateTime.now()
        // required for initialization only
        containerType = ContainerType.UNKNOWN.id
    }

    override fun toString(): String {
        return "CleanEvent(" +
            "id=$id, " +
            "tokenId=$tokenId, " +
            "kpId=$kpId, " +
            "linkedId=$linkedId, " +
            "relatedId=$relatedId, " +
            "whenTime=$whenTime, " +
            "containerType='$containerType', " +
            "containerVolume=$containerVolume, " +
            "containerCount=$containerCount, " +
            "sent=$sent" +
            ")"
    }
}