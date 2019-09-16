package ru.iqsolution.tkoonline

import android.Manifest
import org.joda.time.format.DateTimeFormat

val DANGER_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

val PATTERN_DATE = DateTimeFormat.forPattern("yyyy-MM-dd")

val PATTERN_TIME = DateTimeFormat.forPattern("HH:mm:ssZZ")

val PATTERN_DATETIME = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ")