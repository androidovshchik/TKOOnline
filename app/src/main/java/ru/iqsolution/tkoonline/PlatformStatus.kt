package ru.iqsolution.tkoonline

@Suppress("unused")
enum class PlatformStatus(
    val id: Int,
    val color: Int,
    val drawable: Int
) {
    NO_TASK(0, R.color.colorStatusGray, R.drawable.oval_gray),
    CLEANED(10, R.color.colorStatusGreen, R.drawable.oval_green),
    CLEANED_TIMEOUT(11, R.color.colorStatusGreen, R.drawable.oval_green),
    PENDING(20, R.color.colorStatusYellow, R.drawable.oval_yellow),
    NOT_VISITED(30, R.color.colorStatusRed, R.drawable.oval_red),
    NOT_CLEANED(31, R.color.colorStatusRed, R.drawable.oval_red);

    companion object {

        private val map = values().associateBy(PlatformStatus::id)

        fun fromId(value: Int) = map.getOrDefault(value, NO_TASK)
    }
}