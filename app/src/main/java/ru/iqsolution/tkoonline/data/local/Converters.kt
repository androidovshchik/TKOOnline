package ru.iqsolution.tkoonline.data.local

import androidx.room.TypeConverter
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.PATTERN_DATE
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.PATTERN_TIME

@Suppress("unused")
object Converters {

    @TypeConverter
    @JvmStatic
    fun toDateTime(value: String?): DateTime? {
        return value?.let {
            when {
                it.contains("T") -> DateTime.parse(it, PATTERN_DATETIME)
                it.contains("-") -> DateTime.parse(it, PATTERN_DATE)
                else -> DateTime.parse(it, PATTERN_TIME)
            }
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromDateTime(value: DateTime?): String? {
        // todo check values
        return value?.toString(PATTERN_DATETIME)
    }
}