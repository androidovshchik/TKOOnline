package ru.iqsolution.tkoonline.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDateTime

@Entity(tableName = "events")
class CleanEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id = 0L

    @ColumnInfo(name = "День")
    @SerializedName("time")
    lateinit var time: LocalDateTime

    @ColumnInfo(name = "День")
    @SerializedName("container_type_fact")
    lateinit var containerTypeFact: String

    @ColumnInfo(name = "День")
    @SerializedName("container_type_volume_fact")
    var containerTypeVolumeFact = 0f

    @ColumnInfo(name = "День")
    @SerializedName("container_count_fact")
    var containerCountFact = 0
}