package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.models.PhotoType

class ResponseTypes {

    @SerializedName("data")
    lateinit var data: List<PhotoType>
}