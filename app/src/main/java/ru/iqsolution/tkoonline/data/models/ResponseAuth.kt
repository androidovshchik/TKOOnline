package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class ResponseAuth {

    @SerializedName("access_key")
    lateinit var accessKey: String

    @SerializedName("expire")
    lateinit var expire: String

    @SerializedName("current_time")
    lateinit var currentTime: String

    @SerializedName("no_kp_photo")
    var noKpPhoto = 0
}