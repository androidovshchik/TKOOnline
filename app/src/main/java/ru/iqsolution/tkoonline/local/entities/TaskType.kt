package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "task_types",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["tt_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tt_token_id"])
    ]
)
class TaskType : Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tt_uid")
    var uid: Long? = null

    @SerializedName("id")
    @ColumnInfo(name = "tt_id")
    var id = 0

    @ColumnInfo(name = "tt_token_id")
    var tokenId = 0L

    @NonNull
    @SerializedName("code")
    @ColumnInfo(name = "tt_code")
    lateinit var code: String

    @SerializedName("name")
    @ColumnInfo(name = "tt_name")
    var name: String? = null
}