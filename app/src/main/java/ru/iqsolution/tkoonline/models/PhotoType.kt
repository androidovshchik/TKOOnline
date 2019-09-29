package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PhotoType : Serializable {

    @SerializedName("id")
    var type = 0

    @SerializedName("description")
    lateinit var description: String

    @SerializedName("short_name")
    lateinit var shortName: String

    @SerializedName("is_error")
    var isError = 0
}