package ru.iqsolution.tkoonline.remote.models

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.models.PhotoType

class ResponseTypes {

    @SerializedName("data")
    lateinit var data: List<PhotoType>
}