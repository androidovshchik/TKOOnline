package ru.iqsolution.tkoonline.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.extensions.toInt
import java.io.Serializable

@Entity(
    tableName = "photo_types"
)
class PhotoType : Serializable {

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "pt_id")
    var id = Default.OTHER.id

    @SerializedName("description")
    @ColumnInfo(name = "pt_description")
    lateinit var description: String

    @SerializedName("short_name")
    @ColumnInfo(name = "pt_short_name")
    lateinit var shortName: String

    @SerializedName("is_error")
    @ColumnInfo(name = "pt_is_error")
    var error = 0

    @Suppress("unused")
    enum class Default(
        val id: Int,
        val description: String,
        val isDelete: Boolean,
        val isError: Boolean,
        val shortName: String
    ) {
        BEFORE(0, "До уборки", false, false, "До"),
        AFTER(1, "После уборки", false, false, "Посл"),
        OTHER(16, "Прочее", false, true, "Пр");

        fun toType() = PhotoType().also {
            it.id = id
            it.description = description
            it.shortName = shortName
            it.error = isError.toInt()
        }

        companion object {

            private val map = values().associateBy(Default::id)

            fun fromId(value: Int) = map.getOrDefault(value, OTHER)
        }
    }
}