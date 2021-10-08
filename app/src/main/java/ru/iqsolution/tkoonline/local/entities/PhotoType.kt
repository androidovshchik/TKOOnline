package ru.iqsolution.tkoonline.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "photo_types",
    foreignKeys = [
        ForeignKey(
            entity = Token::class,
            parentColumns = ["t_id"],
            childColumns = ["pt_token_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["pt_token_id"])
    ]
)
class PhotoType : Serializable {

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "pt_id")
    var id = Default.OTHER.id

    @ColumnInfo(name = "pt_token_id")
    var tokenId = 0L

    @NonNull
    @SerializedName("description")
    @ColumnInfo(name = "pt_description")
    lateinit var description: String

    @NonNull
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
            it.error = if (isError) 1 else 0
        }

        companion object {

            private val map = values().associateBy(Default::id)

            fun fromId(value: Int) = map.getOrDefault(value, OTHER)
        }
    }
}