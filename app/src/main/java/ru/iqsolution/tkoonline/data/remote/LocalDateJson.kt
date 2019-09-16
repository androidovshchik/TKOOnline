package ru.iqsolution.tkoonline.data.remote

import com.google.gson.*
import org.joda.time.LocalDate
import ru.iqsolution.tkoonline.PATTERN_DATE
import java.lang.reflect.Type

class LocalDateSerializer : JsonSerializer<LocalDate> {

    override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString(PATTERN_DATE))
    }
}

class LocalDateDeserializer : JsonDeserializer<LocalDate> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
        return LocalDate.parse(json.asString, PATTERN_DATE)
    }
}