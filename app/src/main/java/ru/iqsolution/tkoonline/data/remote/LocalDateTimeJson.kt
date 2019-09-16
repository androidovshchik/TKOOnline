package ru.iqsolution.tkoonline.data.remote

import com.google.gson.*
import org.joda.time.LocalDateTime
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import java.lang.reflect.Type

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {

    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString(PATTERN_DATETIME))
    }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        return LocalDateTime.parse(json.asString, PATTERN_DATETIME)
    }
}