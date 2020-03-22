package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.CleanEvent

class ResponseClean {

    @SerializedName("id")
    lateinit var id: String

    @SerializedName("data")
    lateinit var data: CleanEvent
}