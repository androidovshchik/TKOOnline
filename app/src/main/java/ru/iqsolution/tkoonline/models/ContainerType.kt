package ru.iqsolution.tkoonline.models

import ru.iqsolution.tkoonline.R

@Suppress("unused")
enum class ContainerType(
    // NOTICE it is uppercase
    val id: String?,
    val icon: Int,
    val isEditable: Boolean,
    val shortName: String = id.toString()
) {
    UNKNOWN(null, 0, false),
    REGULAR("ТБО", R.drawable.ic_trash_can, true),
    BUNKER("КГМ", R.drawable.ic_trash_bin, true),
    WITHOUT("БТ", R.drawable.ic_rubbish_bag, false),
    SPECIAL("СПЕЦИАЛЬНЫЙ", R.drawable.ic_triangle, true, "Спец.");

    companion object {

        private val map = values().associateBy(ContainerType::id)

        fun fromId(value: String) = map.getOrDefault(value, UNKNOWN)
    }
}