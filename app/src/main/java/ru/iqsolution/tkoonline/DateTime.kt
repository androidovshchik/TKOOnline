package ru.iqsolution.tkoonline

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

val defaultZone: ZoneOffset
    get() = ZoneId.systemDefault().rules.getOffset(Instant.now())

// для удобства фильтрации по дню (токен сбрасывается в определенное время)
val midnightZone: ZoneOffset = ZoneOffset.ofHours(1)

val patternTime: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.TIME)

val patternTimeZone: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.TIME_ZONE)

val patternTimeMillis: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.TIME_MILLIS)

val patternDate: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.DATE)

val patternDateTimeZone: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.DATETIME_ZONE)

fun ZonedDateTime.isEarlier(offset: Long = 0L, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
    val now = ZonedDateTime.now()
    val millis = unit.toMillis(offset)
    return if (millis > 0L) {
        now.minus(millis, ChronoUnit.MILLIS).isAfter(this)
    } else {
        now.isAfter(this)
    }
}

fun ZonedDateTime.isLater(offset: Long = 0L, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
    val now = ZonedDateTime.now()
    val millis = unit.toMillis(offset)
    return if (millis > 0L) {
        now.plus(millis, ChronoUnit.MILLIS).isBefore(this)
    } else {
        now.isBefore(this)
    }
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Pattern(val format: String) {

    companion object {

        // для отображения
        const val TIME = "HH:mm"

        const val TIME_ZONE = "HH:mm:ssZ"

        // для отладки
        const val TIME_MILLIS = "HH.mm.ss.SSS"

        const val DATE = "yyyyMMdd"

        const val DATETIME_ZONE = "yyyyMMdd'T'HHmmssZ"
    }
}