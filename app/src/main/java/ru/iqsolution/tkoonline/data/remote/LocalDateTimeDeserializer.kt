package ru.iqsolution.tkoonline.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import java.lang.reflect.Type

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        return LocalDateTime.parse(json.asString, PATTERN_DATETIME)
    }

    companion object {

        val PATTERN_DATETIME = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmssZ")
    }
}