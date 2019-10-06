package ru.iqsolution.tkoonline

import android.Manifest
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

const val CHANNEL_DEFAULT = "default_channel"

const val LOCATION_INTERVAL = 5000L

// for password
const val WAIT_TIME = 5 * 60 * 1000L

val DANGER_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION
)

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ssZZ").withOffsetParsed()

val PATTERN_DATETIME: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ").withOffsetParsed()

val FORMAT_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

const val ACTION_LOCATION = "action_location"

const val ACTION_CLOUD = "action_cloud"

const val EXTRA_TELEMETRY_LOCATION = "telemetry_location"

const val EXTRA_TELEMETRY_AVAILABILITY = "telemetry_availability"

const val EXTRA_PLATFORM_PLATFORM = "platform_platform"

const val EXTRA_PLATFORM_PHOTO_TYPES = "platform_photo types"

const val EXTRA_PROBLEM_PLATFORM = "problem_platform"

const val EXTRA_PROBLEM_PHOTO_TYPES = "problem_photo_types"

// not required
const val EXTRA_PHOTO_TITLE = "photo_title"

const val EXTRA_PHOTO_EVENT = "photo_event"

// not required
const val EXTRA_PHOTO_LINKED_IDS = "photo_linked_ids"