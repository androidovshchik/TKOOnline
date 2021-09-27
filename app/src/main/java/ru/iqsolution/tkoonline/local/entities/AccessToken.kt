package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.iqsolution.tkoonline.extensions.Pattern
import java.time.ZonedDateTime

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

    @NonNull
    @ColumnInfo(name = "t_token")
    lateinit var token: String

    @NonNull
    @ColumnInfo(name = "t_que_name")
    lateinit var queName: String

    @ColumnInfo(name = "t_car_id")
    var carId = 0

    @NonNull
    @Pattern(Pattern.DATETIME_ZONE)
    @ColumnInfo(name = "t_expires")
    lateinit var expires: ZonedDateTime

    val authHeader: String
        get() = "Bearer $token"
}