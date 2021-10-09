package ru.iqsolution.tkoonline.remote

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.Pattern
import ru.iqsolution.tkoonline.local.entities.LocationEventToken
import ru.iqsolution.tkoonline.local.entities.TaskEvent
import ru.iqsolution.tkoonline.patternDateTimeZone
import ru.iqsolution.tkoonline.patternTimeZone
import java.lang.reflect.Type
import java.time.OffsetTime
import java.time.ZonedDateTime

class SerializedNameStrategy : ExclusionStrategy {

    override fun shouldSkipField(attributes: FieldAttributes): Boolean {
        return attributes.getAnnotation(SerializedName::class.java) == null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return false
    }
}

class ZonedDateTimeSerializer : JsonSerializer<ZonedDateTime> {

    @Pattern(Pattern.DATETIME_ZONE)
    override fun serialize(src: ZonedDateTime, type: Type, ctx: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.format(patternDateTimeZone))
    }
}

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime> {

    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): ZonedDateTime {
        return ZonedDateTime.parse(json.asString, patternDateTimeZone)
    }
}

class OffsetTimeSerializer : JsonSerializer<OffsetTime> {

    @Pattern(Pattern.DATETIME_ZONE)
    override fun serialize(src: OffsetTime, type: Type, ctx: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.format(patternTimeZone))
    }
}

class OffsetTimeDeserializer : JsonDeserializer<OffsetTime> {

    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): OffsetTime {
        return OffsetTime.parse(json.asString, patternTimeZone)
    }
}

class LocationEventTokenSerializer : JsonSerializer<LocationEventToken> {

    override fun serialize(src: LocationEventToken, type: Type, ctx: JsonSerializationContext): JsonElement {
        val data = src.location.data
        return JsonObject().apply {
            addProperty("id", src.location.packageId)
            addProperty("v", src.location.version)
            addProperty("auth_key", src.token.token)
            add("data", JsonObject().apply {
                addProperty("event_time", data.whenTime.format(patternDateTimeZone))
                addProperty("time", data.locationTime.format(patternDateTimeZone))
                addProperty("lat", data.latitude)
                addProperty("lon", data.longitude)
                addProperty("height", data.altitude)
                addProperty("valid", data.validity)
                addProperty("sat_cnt", data.satellites)
                addProperty("spd", data.speed)
                addProperty("dir", data.direction)
                addProperty("race", data.mileage)
            })
        }
    }
}

class TaskEventSerializer : JsonSerializer<TaskEvent> {

    override fun serialize(src: TaskEvent, type: Type, ctx: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            add("data", JsonObject().apply {
                addProperty("time", src.whenTime.format(patternDateTimeZone))
                addProperty("task_id", src.taskId)
                addProperty("latitude", src.latitude)
                addProperty("longitude", src.longitude)
                addProperty("container_type_fact", src.containerType)
                addProperty("container_type_volume_fact", src.containerVolume)
                addProperty("container_count_fact", src.containerCount)
                addProperty("task_type", src.typeId)
            })
        }
    }
}