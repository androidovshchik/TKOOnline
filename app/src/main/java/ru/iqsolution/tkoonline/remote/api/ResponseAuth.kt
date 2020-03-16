package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

class ResponseAuth {

    @SerializedName("access_key")
    lateinit var accessKey: String

    @SerializedName("expire")
    lateinit var expireTime: String

    /**
     * [ru.iqsolution.tkoonline.extensions.PATTERN_DATETIME_ZONE]
     */
    @SerializedName("current_time")
    lateinit var currentTime: DateTime

    @SerializedName("no_kp_photo")
    var noKpPhoto = 0

    @SerializedName("que_name")
    lateinit var queName: String
}