package ru.iqsolution.tkoonline.models

import ru.iqsolution.tkoonline.R

@Suppress("unused")
enum class ContainerType(
    val id: String?,
    val icon: Int,
    val isEditable: Boolean,
    val shortName: String = id.toString()
) {
    UNKNOWN(null, R.drawable.ic_question_mark, false, "Неиз."),
    REGULAR("ТБО", R.drawable.ic_trash_can, true),
    BUNKER("КГМ", R.drawable.ic_trash_bin, true),
    BULK1("БТ", R.drawable.ic_rubbish_bag, false),
    BULK2("Бестарный", R.drawable.ic_rubbish_bag, false, "БТ"),
    SPECIAL1("специальный", R.drawable.ic_triangle, true, "Спец."),
    SPECIAL2("Специальный", R.drawable.ic_triangle, true, "Спец."),
    SPECIAL3("специализированный", R.drawable.ic_triangle, true, "Спец."),
    SPECIAL4("Специализированный", R.drawable.ic_triangle, true, "Спец.");

    companion object {

        private val map = values().associateBy(ContainerType::id)

        fun fromId(value: String?) = map.getOrDefault(value, UNKNOWN)
    }
}