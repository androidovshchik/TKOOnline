package ru.iqsolution.tkoonline.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.io.Serializable

@Entity(
    tableName = "contacts"
)
class Contact : Serializable {

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "c_id")
    var id = 0

    @SerializedName("name")
    @ColumnInfo(name = "c_name")
    var name: String? = null

    @SerializedName("phone")
    @ColumnInfo(name = "c_phone")
    var phone: String? = null

    @SerializedName("last_login_date")
    @ColumnInfo(name = "c_when_logged")
    var whenLogged: DateTime? = null
}