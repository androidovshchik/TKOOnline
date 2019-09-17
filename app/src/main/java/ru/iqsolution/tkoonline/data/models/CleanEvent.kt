package ru.iqsolution.tkoonline.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

@Entity(tableName = "events")
class CleanEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0L

    @ColumnInfo(name = "access_token")
    var accessToken: String? = null

    @ColumnInfo(name = "datetime")
    @SerializedName("time")
    lateinit var time: DateTime

    @ColumnInfo(name = "container_type")
    @SerializedName("container_type_fact")
    lateinit var containerTypeFact: String

    @ColumnInfo(name = "container_volume")
    @SerializedName("container_type_volume_fact")
    var containerTypeVolumeFact = 0f

    @ColumnInfo(name = "container_count")
    @SerializedName("container_count_fact")
    var containerCountFact = 0
}