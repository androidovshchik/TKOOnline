package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.CleanEvent

open class RequestClean {

    @SerializedName("data")
    lateinit var data: CleanEvent
}