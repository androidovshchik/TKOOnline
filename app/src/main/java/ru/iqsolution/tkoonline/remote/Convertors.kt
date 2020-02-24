package ru.iqsolution.tkoonline.remote

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.PATTERN_TIME_ZONE
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

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE] is only supported
     */
    override fun serialize(src: DateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString(PATTERN_DATETIME_ZONE))
    }
}

class DateTimeDeserializer : JsonDeserializer<DateTime> {

    /**
     * NOTICE [ru.iqsolution.tkoonline.PATTERN_DATE] is not supported
     */
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime {
        val value = json.asString
        return when {
            value.contains("T") -> DateTime.parse(json.asString, PATTERN_DATETIME_ZONE)
            else -> DateTime.parse(json.asString, PATTERN_TIME_ZONE)
        }
    }
}