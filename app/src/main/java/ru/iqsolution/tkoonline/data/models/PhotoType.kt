package ru.iqsolution.tkoonline.data.models

@Suppress("unused")
enum class PhotoType(
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

        val map = values().associateBy(PhotoType::id)

        fun fromId(value: Int?): PhotoType? = value?.let { map[value] }
    }
}