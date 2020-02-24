package ru.iqsolution.tkoonline.local

import androidx.room.TypeConverter
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.PATTERN_TIME_ZONE

@Suppress("unused")
object Converters {

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE] is only supported
     */
    @TypeConverter
    @JvmStatic
    fun fromDateTime(value: DateTime?): String? {
        return value?.toString(PATTERN_DATETIME_ZONE)
    }

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATE] is not supported
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