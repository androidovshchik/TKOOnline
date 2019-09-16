package ru.iqsolution.tkoonline.data.remote

import com.google.gson.*
import org.joda.time.LocalTime
import ru.iqsolution.tkoonline.PATTERN_TIME
import java.lang.reflect.Type

class LocalTimeSerializer : JsonSerializer<LocalTime> {

    override fun serialize(src: LocalTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString(PATTERN_TIME))
    }
}

class LocalTimeDeserializer : JsonDeserializer<LocalTime> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalTime {
        return LocalTime.parse(json.asString, PATTERN_TIME)
    }
}