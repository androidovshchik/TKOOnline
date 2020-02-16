package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName

class ResponseVersion {

    @SerializedName("version")
    var version = 0

    @SerializedName("url")
    lateinit var url: String
}