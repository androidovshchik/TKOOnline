package ru.iqsolution.tkoonline.local

import androidx.room.TypeConverter
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.extensions.PATTERN_DATE
import ru.iqsolution.tkoonline.extensions.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.extensions.PATTERN_TIME_ZONE
import ru.iqsolution.tkoonline.extensions.Pattern

@Suppress("unused")
object Converters {

    @TypeConverter
    @Pattern(Pattern.DATETIME_ZONE)
    @JvmStatic
    fun fromDateTime(value: DateTime?): String? {
        return value?.toString(PATTERN_DATETIME_ZONE)
    }

    /**
     * NOTICE [PATTERN_DATE] is not supported
     */
    @TypeConverter
    @JvmStatic
    fun toDateTime(value: String?): DateTime? {
        return value?.let {
            when {
                it.contains("T") -> DateTime.parse(it, PATTERN_DATETIME_ZONE)
                else -> DateTime.parse(it, PATTERN_TIME_ZONE)
            }
        }
    }
}