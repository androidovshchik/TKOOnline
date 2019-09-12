package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDateTime

class ResponseAuth {

    @SerializedName("access_key")
    lateinit var accessKey: String

    @SerializedName("expire")
    lateinit var expire: LocalDateTime

    @SerializedName("current_time")
    lateinit var currentTime: LocalDateTime

    @SerializedName("no_kp_photo")
    var noKpPhoto = 0
}