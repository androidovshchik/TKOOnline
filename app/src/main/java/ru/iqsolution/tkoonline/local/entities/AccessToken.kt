package ru.iqsolution.tkoonline.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(
    tableName = "tokens",
    indices = [
        Index(value = ["t_token"], unique = true)
    ]
)
class AccessToken {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "t_id")
    var id: Long? = null

    @ColumnInfo(name = "t_token")
    lateinit var token: String

    @ColumnInfo(name = "t_que_name")
    lateinit var queName: String

    @ColumnInfo(name = "t_car_id")
    var carId = 0

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @ColumnInfo(name = "t_expires")
    lateinit var expires: DateTime

    val authHeader: String
        get() = "Bearer $token"
}