package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

open class RequestEvent {

    @SerializedName("data")
    lateinit var data: List<CleaningEvent>
}