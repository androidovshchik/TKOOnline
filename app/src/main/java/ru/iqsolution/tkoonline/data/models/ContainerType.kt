package ru.iqsolution.tkoonline.data.models

@Suppress("unused")
enum class ContainerType(
    val id: String?
) {
    UNKNOWN(null),
    REGULAR("ТБО"),
    BUNKER("КГМ"),
    WITHOUT("БТ"),
    SPECIAL("СПЕЦИАЛЬНЫЙ");

    companion object {

        private val map = values().associateBy(ContainerType::id)

        fun fromId(value: String) = map.getOrDefault(value, UNKNOWN)
    }
}