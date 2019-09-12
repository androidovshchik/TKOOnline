package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDateTime

class CleaningEvent {

    @SerializedName("time")
    lateinit var time: LocalDateTime

    @SerializedName("container_type_fact")
    lateinit var containerTypeFact: String

    @SerializedName("container_type_volume_fact")
    var containerTypeVolumeFact = 0f

    @SerializedName("container_count_fact")
    var containerCountFact = 0
}