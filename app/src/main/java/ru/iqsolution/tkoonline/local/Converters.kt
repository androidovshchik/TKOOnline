package ru.iqsolution.tkoonline.local

import androidx.room.TypeConverter
import ru.iqsolution.tkoonline.Pattern
import ru.iqsolution.tkoonline.patternDate
import ru.iqsolution.tkoonline.patternDateTimeZone
import ru.iqsolution.tkoonline.patternTimeZone
import java.time.LocalDate
import java.time.OffsetTime
import java.time.ZonedDateTime

@Suppress("unused")
object Converters {

    @JvmStatic
    @TypeConverter
    @Pattern(Pattern.TIME_ZONE)
    fun fromOffsetTime(value: OffsetTime?): String? {
        return value?.format(patternTimeZone)
    }

    @JvmStatic
    @TypeConverter
    @Pattern(Pattern.TIME_ZONE)
    fun toOffsetTime(value: String?): OffsetTime? {
        return value?.let { OffsetTime.parse(it, patternTimeZone) }
    }

    @JvmStatic
    @TypeConverter
    @Pattern(Pattern.DATE)
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(patternDate)
    }

    @JvmStatic
    @TypeConverter
    @Pattern(Pattern.DATE)
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, patternDate) }
    }

    @JvmStatic
    @TypeConverter
    @Pattern(Pattern.DATETIME_ZONE)
    fun fromZonedDateTime(value: ZonedDateTime?): String? {
        return value?.format(patternDateTimeZone)
    }

    @JvmStatic
    @TypeConverter
    fun toZonedDateTime(value: String?): ZonedDateTime? {
        return value?.let { ZonedDateTime.parse(it, patternDateTimeZone) }
    }
}