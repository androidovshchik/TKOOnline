package ru.iqsolution.tkoonline

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

const val DB_NAME = "app.db"

const val CHANNEL_DEFAULT = "default_channel"

const val PASSWORD_RETRY = 5 * 60 * 1000L

val PATTERN_DATE: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

val PATTERN_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ssZZ").withOffsetParsed()

val PATTERN_DATETIME: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ").withOffsetParsed()

val FORMAT_TIME: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

const val ACTION_LOCATION = "action_location"

const val ACTION_ROUTE = "action_route"

const val ACTION_CLOUD = "action_cloud"

const val EXTRA_TELEMETRY_TASK = "telemetry_task"

const val EXTRA_SYNC_LOCATION = "sync_coordinates"

const val EXTRA_SYNC_AVAILABILITY = "sync_availability"

const val EXTRA_PLATFORM = "platform"

const val EXTRA_PHOTO_TYPES = "photo_types"

const val EXTRA_PHOTO_EVENT = "photo_event"

const val EXTRA_PHOTO_PATH = "photo_path"

// not required
const val EXTRA_PHOTO_TITLE = "photo_title"

// not required
const val EXTRA_PHOTO_LINKED_IDS = "photo_linked_ids"