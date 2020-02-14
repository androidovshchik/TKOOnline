package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PhotoType : Serializable {

    @SerializedName("id")
    var id = Default.OTHER.id

    @SerializedName("description")
    lateinit var description: String

    @SerializedName("short_name")
    lateinit var shortName: String

    @SerializedName("is_error")
    var isError = 0

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
            it.isError = if (isError) 1 else 0
        }

        companion object {

            private val map = values().associateBy(Default::id)

            fun fromId(value: Int) = map.getOrDefault(value, OTHER)
        }
    }
}