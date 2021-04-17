package ru.iqsolution.tkoonline.extensions

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Pattern(val format: String) {

    companion object {

        const val DATE = "yyyyMMdd"

        const val TIME_ZONE = "HH:mm:ssZZ"

        const val DATETIME_ZONE = "yyyyMMdd'T'HHmmssZ"

        const val TIME = "HH:mm"

        const val TIME_MILLIS = "HH.mm.ss.SSS"
    }
}

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern(Pattern.DATE)

val PATTERN_TIME_ZONE: DateTimeFormatter =
    DateTimeFormat.forPattern(Pattern.TIME_ZONE).withOffsetParsed()

val PATTERN_DATETIME_ZONE: DateTimeFormatter =
    DateTimeFormat.forPattern(Pattern.DATETIME_ZONE).withOffsetParsed()

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern(Pattern.TIME)

val PATTERN_TIME_MILLIS: DateTimeFormatter = DateTimeFormat.forPattern(Pattern.TIME_MILLIS)

// do not compare with the past
fun DateTime.isLater(plus: Long = 0L, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
    val now = DateTime.now()
    val duration = unit.toMillis(plus)
    return if (duration > 0L) {
        now.plus(duration).isBefore(withZone(now.zone))
    } else {
        now.isBefore(withZone(now.zone))
    }
}

// do not compare with the future
fun DateTime.isEarlier(minus: Long = 0L, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
    val now = DateTime.now()
    val duration = unit.toMillis(minus)
    return if (duration > 0L) {
        now.minus(duration).isAfter(withZone(now.zone))
    } else {
        now.isAfter(withZone(now.zone))
    }
}