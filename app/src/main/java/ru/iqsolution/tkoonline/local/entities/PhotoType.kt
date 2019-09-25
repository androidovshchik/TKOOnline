package ru.iqsolution.tkoonline.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "photo_types",
    foreignKeys = [
        ForeignKey(
            entity = AccessToken::class,
            parentColumns = ["t_id"],
            childColumns = ["pt_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class PhotoType {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pt_id")
    var id: Long? = null

    @ColumnInfo(name = "pt_token_id", index = true)
    var tokenId = 0L

    @ColumnInfo(name = "pt_type")
    @SerializedName("id")
    var type = 0

    @ColumnInfo(name = "pt_description")
    @SerializedName("description")
    lateinit var description: String

    @ColumnInfo(name = "pt_short_name")
    @SerializedName("short_name")
    lateinit var shortName: String

    @ColumnInfo(name = "pt_is_error")
    @SerializedName("is_error")
    var isError = 0
}