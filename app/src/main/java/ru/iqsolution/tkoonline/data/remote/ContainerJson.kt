package ru.iqsolution.tkoonline.data.remote

import com.google.gson.*
import ru.iqsolution.tkoonline.data.models.ContainerStatus
import ru.iqsolution.tkoonline.data.models.ContainerType
import java.lang.reflect.Type

class ContainerStatusSerializer : JsonSerializer<ContainerStatus> {

    override fun serialize(src: ContainerStatus, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }
}

class ContainerStatusDeserializer : JsonDeserializer<ContainerStatus> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ContainerStatus {
        return ContainerStatus.fromId(json.asInt)
    }
}

class ContainerTypeSerializer : JsonSerializer<ContainerType> {

    override fun serialize(src: ContainerType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }
}

class ContainerTypeDeserializer : JsonDeserializer<ContainerType> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ContainerType {
        return ContainerType.fromId(json.asString.toUpperCase())
    }
}