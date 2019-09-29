package ru.iqsolution.tkoonline.remote

import com.google.gson.*
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.PATTERN_TIME
import ru.iqsolution.tkoonline.PlatformStatus
import java.lang.reflect.Type

class DateTimeSerializer : JsonSerializer<DateTime> {

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATETIME] is only supported
     */
    override fun serialize(src: DateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString(PATTERN_DATETIME))
    }
}

class DateTimeDeserializer : JsonDeserializer<DateTime> {

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATE] is not supported
     */
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime {
        val value = json.asString
        return when {
            value.contains("T") -> DateTime.parse(json.asString, PATTERN_DATETIME)
            else -> DateTime.parse(json.asString, PATTERN_TIME)
        }
    }
}

class PlatformStatusSerializer : JsonSerializer<PlatformStatus> {

    override fun serialize(src: PlatformStatus, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }
}

class PlatformStatusDeserializer : JsonDeserializer<PlatformStatus> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PlatformStatus {
        return PlatformStatus.fromId(json.asInt)
    }
}