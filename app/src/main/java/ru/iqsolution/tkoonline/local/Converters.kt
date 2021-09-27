package ru.iqsolution.tkoonline.local

import androidx.room.TypeConverter
import ru.iqsolution.tkoonline.extensions.Pattern
import ru.iqsolution.tkoonline.extensions.patternDate
import ru.iqsolution.tkoonline.extensions.patternDateTimeZone
import ru.iqsolution.tkoonline.extensions.patternTimeZone
import java.time.ZonedDateTime

@Suppress("unused")
object Converters {

    @JvmStatic
    @TypeConverter
    @Pattern(Pattern.DATETIME_ZONE)
    fun fromZonedDateTime(value: ZonedDateTime?): String? {
        return value?.format(patternDateTimeZone)
    }

    /**
     * NOTICE [patternDate] is not supported
     */
    @JvmStatic
    @TypeConverter
    fun toZonedDateTime(value: String?): ZonedDateTime? {
        return value?.let {
            when {
                it.contains("T") -> ZonedDateTime.parse(it, patternDateTimeZone)
                else -> ZonedDateTime.parse(it, patternTimeZone)
            }
        }
    }
}