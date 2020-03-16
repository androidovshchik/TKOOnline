package ru.iqsolution.tkoonline.extensions

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

val PATTERN_TIME_ZONE: DateTimeFormatter =
    DateTimeFormat.forPattern("HH:mm:ssZZ").withOffsetParsed()

val PATTERN_DATETIME_ZONE: DateTimeFormatter =
    DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ").withOffsetParsed()

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")