package ru.iqsolution.tkoonline

import android.Manifest
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

val DANGER_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION
)

const val CHANNEL_DEFAULT = "default_channel"

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ssZZ").withOffsetParsed()

val PATTERN_DATETIME: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ").withOffsetParsed()

val FORMAT_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

/**
 * Extras
 */

const val EXTRA_ID = "extra_id"