package ru.iqsolution.tkoonline.models

import android.annotation.SuppressLint
import ru.iqsolution.tkoonline.R

@Suppress("unused")
enum class ContainerType(
    val id: String,
    val icon: Int,
    val defVolume: Float,
    val isEditable: Boolean,
    val shortName: String = id
) {
    // UNKNOWN.id is only my own implementation
    UNKNOWN("НЕИЗВЕСТНЫЙ", R.drawable.ic_question, 0f, false, "Неиз."),
    REGULAR("ТБО", R.drawable.ic_trash_can, 0.8f, true),
    BUNKER("КГМ", R.drawable.ic_trash_bin, 8f, true),
    BULK1("БТ", R.drawable.ic_rubbish_bag, 1f, false),
    BULK2("БЕСТАРНЫЙ", R.drawable.ic_rubbish_bag, 1f, false, "БТ"),
    SPECIAL1("СПЕЦИАЛЬНЫЙ", R.drawable.ic_triangle, 1.1f, true, "Спец."),
    SPECIAL2("СПЕЦИАЛИЗИРОВАННЫЙ", R.drawable.ic_triangle, 1.1f, true, "Спец.");

    companion object {

        private val map = values().associateBy(ContainerType::id)

        @SuppressLint("DefaultLocale")
        fun fromId(value: String?) = map.getOrDefault(value?.toUpperCase(), UNKNOWN)
    }
}