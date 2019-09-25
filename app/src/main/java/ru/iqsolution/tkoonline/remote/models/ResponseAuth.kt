package ru.iqsolution.tkoonline.remote.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

class ResponseAuth {

    @SerializedName("access_key")
    lateinit var accessKey: String

    @SerializedName("expire")
    lateinit var expire: String

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @SerializedName("current_time")
    lateinit var currentTime: DateTime

    @SerializedName("no_kp_photo")
    var noKpPhoto = 0

    @SerializedName("que_name")
    lateinit var queName: String
}