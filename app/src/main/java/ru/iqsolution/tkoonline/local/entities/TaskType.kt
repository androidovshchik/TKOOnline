package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "task_types"
)
class TaskType : Serializable {

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "tt_id")
    var id = 0

    @NonNull
    @SerializedName("code")
    @ColumnInfo(name = "tt_code")
    lateinit var code: String

    @SerializedName("name")
    @ColumnInfo(name = "tt_name")
    var name: String? = null
}