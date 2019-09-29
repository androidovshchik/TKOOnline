package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.models.Platform

class ResponsePlatforms {

    @SerializedName("data")
    lateinit var data: List<Platform>
}