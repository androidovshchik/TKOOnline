package ru.iqsolution.tkoonline

import android.Manifest
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

val DANGER_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ssZZ")

val PATTERN_DATETIME: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ")

/**
 * Extras
 */

const val EXTRA_PROMPT = "extra_prompt"