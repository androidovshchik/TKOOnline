package ru.iqsolution.tkoonline.data.remote

import com.google.gson.*
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.PATTERN_DATE
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.PATTERN_TIME
import java.lang.reflect.Type

class LocalDateTimeSerializer : JsonSerializer<DateTime> {

    override fun serialize(src: DateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        // todo check values
        return JsonPrimitive(src.toString(PATTERN_DATETIME))
    }
}

class LocalDateTimeDeserializer : JsonDeserializer<DateTime> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime {
        val value = json.asString
        return when {
            value.contains("T") -> DateTime.parse(json.asString, PATTERN_DATETIME)
            value.contains("-") -> DateTime.parse(json.asString, PATTERN_DATE)
            else -> DateTime.parse(json.asString, PATTERN_TIME)
        }
    }
}