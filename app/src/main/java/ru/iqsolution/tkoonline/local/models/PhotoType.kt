package ru.iqsolution.tkoonline.local.models

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class PhotoType {

    @SerializedName("id")
    var id = 0

    @SerializedName("description")
    lateinit var description: String

    @SerializedName("is_error")
    var isError = 0

    enum class Default(
        val id: Int,
        val description: String,
        val isDelete: Boolean,
        val isError: Boolean,
        val shortName: String
    ) {
        BEFORE(0, "До", false, false, "До"),
        AFTER(1, "После", false, false, "Посл"),
        ROAD(11, "Нет проезда", false, true, "НетПр"),
        CONTRACT(12, "Мусор не соответствует договору", false, true, "МнСД"),
        EMPTY(13, "Пустой бак", false, true, "Пуст"),
        DAMAGED(14, "Контейнер повреждён", false, true, "Повр"),
        DIRTY(15, "Площадка требует уборки", false, true, "ГрПл"),
        OTHER(16, "Прочее", false, true, "Пр");

        companion object {

            private val map = values().associateBy(Default::id)

            fun fromId(value: Int): Default? = map[value]
        }
    }
}