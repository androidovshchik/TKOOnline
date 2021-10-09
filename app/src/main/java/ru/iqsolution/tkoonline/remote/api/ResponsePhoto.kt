package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.PhotoEvent

class ResponsePhoto {

    @SerializedName("id")
    var id = 0

    @SerializedName("data")
    lateinit var event: PhotoEvent
}