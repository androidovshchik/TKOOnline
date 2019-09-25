package ru.iqsolution.tkoonline.remote.models

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.PhotoEvent

class ResponsePhoto {

    @SerializedName("id")
    lateinit var id: String

    @SerializedName("data")
    lateinit var data: PhotoEvent
}