package ru.iqsolution.tkoonline.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

@Entity(tableName = "cleanup")
class PhotoEvent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "kp_id")
    var kpId = 0

    @ColumnInfo(name = "access_token")
    var accessToken: String? = null

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "datetime")
    @SerializedName("time")
    lateinit var datetime: DateTime

    @ColumnInfo(name = "container_type")
    @SerializedName("container_type_fact")
    lateinit var containerType: String

    @ColumnInfo(name = "container_volume")
    @SerializedName("container_type_volume_fact")
    var containerVolume = 0f

    @ColumnInfo(name = "container_count")
    @SerializedName("container_count_fact")
    var containerCount = 0
}