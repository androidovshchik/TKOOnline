package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.models.Container

@Entity(
    tableName = "clean_events",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["ce_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ce_token_id"])
    ]
)
class CleanEvent : Container, SendEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ce_id")
    var id: Long? = null

    @ColumnInfo(name = "ce_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "ce_kp_id")
    var kpId = 0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "ce_datetime")
    @SerializedName("time")
    lateinit var datetime: DateTime

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

    override fun addContainer(container: Container?) {
        throw IllegalAccessException("Should not be called at all")
    }
}