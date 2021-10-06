package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

@Entity(
    tableName = "routes",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["r_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["r_token_id"])
    ]
)
class Route : Serializable {

    @ColumnInfo(name = "r_token_id")
    var tokenId = 0L

    @PrimaryKey
    @SerializedName("route_number")
    @ColumnInfo(name = "r_id")
    var number: String? = null

    @SerializedName("fio")
    @ColumnInfo(name = "r_fio")
    var fio: String? = null

    @SerializedName("count")
    @ColumnInfo(name = "r_count")
    var count = 0

    @SerializedName("wait_count")
    @ColumnInfo(name = "r_wait")
    var waitCount = 0

    @NonNull
    @ColumnInfo(name = "r_day")
    lateinit var day: LocalDate

    val isDone: Boolean
        get() = waitCount == count
}