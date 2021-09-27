package ru.iqsolution.tkoonline.extensions

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Pattern(val format: String) {

    companion object {

        const val TIME = "HH:mm"

        const val TIME_MILLIS = "HH.mm.ss.SSS"

        const val TIME_ZONE = "HH:mm:ssZZ"

        const val DATE = "yyyyMMdd"

        const val DATETIME_ZONE = "yyyyMMdd'T'HHmmssZ"
    }
}

val PATTERN_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.TIME)

val PATTERN_TIME_MILLIS: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.TIME_MILLIS)

val PATTERN_TIME_ZONE: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.TIME_ZONE)

val PATTERN_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.DATE)

val PATTERN_DATETIME_ZONE: DateTimeFormatter = DateTimeFormatter.ofPattern(Pattern.DATETIME_ZONE)

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