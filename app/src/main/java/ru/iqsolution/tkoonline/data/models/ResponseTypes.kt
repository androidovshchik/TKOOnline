package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class ResponseTypes {

    @SerializedName("data")
    lateinit var data: List<PhotoType>
}