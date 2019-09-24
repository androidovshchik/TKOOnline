package ru.iqsolution.tkoonline.remote.models

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.models.Platform

class ResponsePlatforms {

    @SerializedName("data")
    lateinit var data: List<Platform>
}