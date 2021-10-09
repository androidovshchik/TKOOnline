package ru.iqsolution.tkoonline.local.entities

import androidx.room.*
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.extensions.asPhone
import java.io.Serializable
import java.time.ZonedDateTime

@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["c_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["c_token_id"]),
        Index(value = ["c_phone"], unique = true)
    ]
)
class Contact : Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "c_uid")
    var uid: Long? = null

    @SerializedName("id")
    @ColumnInfo(name = "c_id")
    var id = 0L

    @ColumnInfo(name = "c_token_id")
    var tokenId = 0L

    @SerializedName("name")
    @ColumnInfo(name = "c_name")
    var name: String? = null

    @SerializedName("phone")
    @ColumnInfo(name = "c_phone")
    var phone: String? = null
        set(value) {
            field = value?.asPhone
        }

    @SerializedName("last_login_date")
    @ColumnInfo(name = "c_when_logged")
    var whenLogged: ZonedDateTime? = null
}