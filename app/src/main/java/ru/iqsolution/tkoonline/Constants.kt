package ru.iqsolution.tkoonline

import android.Manifest
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

val DANGER_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION
)

const val MAPKIT_KEY = "9ef633b4-95b2-409a-a74f-e0a5c75321db"

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ssZZ")

val PATTERN_DATETIME: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ")

val FORMAT_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

/**
 * Extras
 */