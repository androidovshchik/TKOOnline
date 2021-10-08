package ru.iqsolution.tkoonline.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.extensions.asPhone
import java.io.Serializable
import java.time.ZonedDateTime

@Entity(
    tableName = "contacts",
    indices = [
        Index(value = ["c_phone"], unique = true)
    ]
)
class Contact : Serializable {

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "c_id")
    var id = 0L

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