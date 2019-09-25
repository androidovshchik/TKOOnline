package ru.iqsolution.tkoonline.local

import androidx.room.TypeConverter
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.PATTERN_TIME
import ru.iqsolution.tkoonline.models.PlatformStatus

@Suppress("unused")
object Converters {

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATETIME] is only supported
     */
    @TypeConverter
    @JvmStatic
    fun fromDateTime(value: DateTime?): String? {
        return value?.toString(PATTERN_DATETIME)
    }

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATE] is not supported
     */
    @TypeConverter
    @JvmStatic
    fun toDateTime(value: String?): DateTime? {
        return value?.let {
            when {
                it.contains("T") -> DateTime.parse(it, PATTERN_DATETIME)
                else -> DateTime.parse(it, PATTERN_TIME)
            }
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromPlatformStatus(value: PlatformStatus?): Int? {
        return value?.id
    }

    @TypeConverter
    @JvmStatic
    fun toPlatformStatus(value: Int?): PlatformStatus? {
        return value?.let {
            return PlatformStatus.fromId(it)
        }
    }
}