package ru.iqsolution.tkoonline

import android.Manifest
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

const val CHANNEL_DEFAULT = "default_channel"

val DANGER_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION
)

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ssZZ").withOffsetParsed()

val PATTERN_DATETIME: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ").withOffsetParsed()

val FORMAT_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

const val ACTION_LOCATION = "action_location"

/**
 * Telemetry events
 */

const val EXTRA_LOCATION = "extra_location"

const val EXTRA_AVAILABILITY = "extra_availability"

/**
 * Platform
 */

const val EXTRA_PLATFORM = "extra_platform"

const val EXTRA_PHOTO_TYPES = "extra_photo types"

/**
 * Platform callback
 */

// + Photo
const val EXTRA_ID = "extra_id"

const val EXTRA_ERRORS = "extra_errors"

const val EXTRA_PHOTO_TITLE = "photo_title"

/**
 * if only photo event exists otherwise [EXTRA_PHOTO_KP_ID] and [EXTRA_PHOTO_TYPE] must be present
 */
const val EXTRA_PHOTO_EVENT = "photo_event"

const val EXTRA_PHOTO_KP_ID = "photo_kp_id"

const val EXTRA_PHOTO_TYPE = "photo_type"

const val EXTRA_PROBLEM_PLATFORM = "problem_platform"

const val EXTRA_PROBLEM_PHOTO_TYPES = "problem_photo_types"