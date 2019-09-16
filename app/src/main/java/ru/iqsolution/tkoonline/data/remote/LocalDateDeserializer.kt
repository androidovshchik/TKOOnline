package ru.iqsolution.tkoonline.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.lang.reflect.Type

class LocalDateDeserializer : JsonDeserializer<LocalDate> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
        return LocalDate.parse(json.asString, PATTERN_DATE)
    }

    companion object {

        val PATTERN_DATE = DateTimeFormat.forPattern("yyyy-MM-dd")
    }
}