package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

class ResponseAuth {

    @SerializedName("access_key")
    lateinit var accessKey: String

    @SerializedName("expire")
    lateinit var expire: Date

    @SerializedName("current_time")
    lateinit var currentTime: Date

    @SerializedName("no_kp_photo")
    var noKpPhoto = 0
}