package ru.iqsolution.tkoonline.remote

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.extensions.Pattern
import ru.iqsolution.tkoonline.extensions.patternDate
import ru.iqsolution.tkoonline.extensions.patternDateTimeZone
import ru.iqsolution.tkoonline.extensions.patternTimeZone
import ru.iqsolution.tkoonline.local.entities.CleanEventToken
import ru.iqsolution.tkoonline.local.entities.LocationEventToken
import java.lang.reflect.Type
import java.time.ZonedDateTime

class SerializedNameStrategy : ExclusionStrategy {

    override fun shouldSkipField(attributes: FieldAttributes): Boolean {
        return attributes.getAnnotation(SerializedName::class.java) == null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return false
    }
}

class DateTimeSerializer : JsonSerializer<ZonedDateTime> {

    @Pattern(Pattern.DATETIME_ZONE)
    override fun serialize(
        src: ZonedDateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.format(patternDateTimeZone))
    }
}

class DateTimeDeserializer : JsonDeserializer<ZonedDateTime> {

    /**
     * NOTICE [patternDate] is not supported
     */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ZonedDateTime {
        val value = json.asString
        return when {
            value.contains("T") -> ZonedDateTime.parse(json.asString, patternDateTimeZone)
            else -> ZonedDateTime.parse(json.asString, patternTimeZone)
        }
    }
}

class LocationEventTokenSerializer : JsonSerializer<LocationEventToken> {

    override fun serialize(src: LocationEventToken, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("id", src.location.packageId)
            addProperty("v", src.location.version)
            addProperty("auth_key", src.token.token)
            add("data", JsonObject().apply {
                addProperty("event_time", src.location.data.whenTime.format(patternDateTimeZone))
                addProperty("time", src.location.data.locationTime.format(patternDateTimeZone))
                addProperty("lat", src.location.data.latitude)
                addProperty("lon", src.location.data.longitude)
                addProperty("height", src.location.data.altitude)
                addProperty("valid", src.location.data.validity)
                addProperty("sat_cnt", src.location.data.satellites)
                addProperty("spd", src.location.data.speed)
                addProperty("dir", src.location.data.direction)
                addProperty("race", src.location.data.mileage)
            })
        }
    }
}

class CleanEventTokenSerializer : JsonSerializer<CleanEventToken> {

    override fun serialize(
        src: CleanEventToken,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonObject().apply {
            add("data", JsonObject().apply {
                addProperty("time", src.clean.whenTime.format(patternDateTimeZone))
                addProperty("container_type_fact", src.clean.containerType)
                addProperty("container_type_volume_fact", src.clean.containerVolume)
                addProperty("container_count_fact", src.clean.containerCount)
            })
        }
    }
}