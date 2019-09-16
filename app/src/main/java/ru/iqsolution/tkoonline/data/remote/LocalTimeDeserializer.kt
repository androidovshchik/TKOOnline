package ru.iqsolution.tkoonline.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalTime
import java.lang.reflect.Type

class LocalTimeDeserializer : JsonDeserializer<LocalTime> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalTime {
        return LocalTime.parse(json.asString, PATTERN_TIME)
    }
}