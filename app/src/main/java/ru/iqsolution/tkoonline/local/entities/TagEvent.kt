package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.extensions.Pattern
import ru.iqsolution.tkoonline.models.Container

@Entity(
    tableName = "tag_events",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["te_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["te_token_id"])
    ]
)
class TagEvent : Container {

    @PrimaryKey
    @ColumnInfo(name = "te_id")
    var id = 0L

    @ColumnInfo(name = "te_token_id")
    var tokenId = 0L

    @ColumnInfo(name = "te_kp_id")
    override var kpId = 0

    @NonNull
    @Pattern(Pattern.DATETIME_ZONE)
    @ColumnInfo(name = "te_when_time")
    lateinit var whenTime: DateTime

    @NonNull
    @ColumnInfo(name = "te_container_type")
    override lateinit var containerType: String

    @ColumnInfo(name = "te_container_volume")
    override var containerVolume = 0f

    @Ignore
    override var containerCount = 0
}