package ru.iqsolution.tkoonline.remote

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.extensions.PATTERN_DATE
import ru.iqsolution.tkoonline.extensions.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.extensions.PATTERN_TIME_ZONE
import ru.iqsolution.tkoonline.extensions.Pattern
import ru.iqsolution.tkoonline.local.entities.CleanEventToken
import ru.iqsolution.tkoonline.local.entities.LocationEventToken
import java.lang.reflect.Type

class SerializedNameStrategy : ExclusionStrategy {

    override fun shouldSkipField(attributes: FieldAttributes): Boolean {
        return attributes.getAnnotation(SerializedName::class.java) == null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return false
    }
}

class DateTimeSerializer : JsonSerializer<DateTime> {

    @Pattern(Pattern.DATETIME_ZONE)
    override fun serialize(
        src: DateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.toString(PATTERN_DATETIME_ZONE))
    }
}

class DateTimeDeserializer : JsonDeserializer<DateTime> {

    /**
     * NOTICE [PATTERN_DATE] is not supported
     */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): DateTime {
        val value = json.asString
        return when {
            value.contains("T") -> DateTime.parse(json.asString, PATTERN_DATETIME_ZONE)
            else -> DateTime.parse(json.asString, PATTERN_TIME_ZONE)
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
                addProperty("event_time", src.location.data.whenTime.toString(PATTERN_DATETIME_ZONE))
                addProperty("time", src.location.data.locationTime.toString(PATTERN_DATETIME_ZONE))
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
                addProperty("time", src.clean.whenTime.toString(PATTERN_DATETIME_ZONE))
                addProperty("container_type_fact", src.clean.containerType)
                addProperty("container_type_volume_fact", src.clean.containerVolume)
                addProperty("container_count_fact", src.clean.containerCount)
            })
        }
    }
}