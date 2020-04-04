package ru.iqsolution.tkoonline.extensions

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

val PATTERN_TIME_ZONE: DateTimeFormatter =
    DateTimeFormat.forPattern("HH:mm:ssZZ").withOffsetParsed()

val PATTERN_DATETIME_ZONE: DateTimeFormatter =
    DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ").withOffsetParsed()

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

val PATTERN_TIME_MILLIS: DateTimeFormatter = DateTimeFormat.forPattern("HH.mm.ss.SSS")

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