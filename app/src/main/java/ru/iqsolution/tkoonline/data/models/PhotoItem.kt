package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class PhotoItem {

    @SerializedName("id")
    var id = 0

    @SerializedName("description")
    lateinit var description: String

    @SerializedName("is_error")
    var isError = 0
}