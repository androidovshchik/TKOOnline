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

fun DateTime.isFuture(duration: Long = 0L, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
    val now = DateTime.now()
    return now.isBefore(withZone(now.zone).plus(unit.toMillis(duration)))
}