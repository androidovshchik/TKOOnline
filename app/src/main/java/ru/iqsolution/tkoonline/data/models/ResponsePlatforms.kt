package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class ResponsePlatforms {

    @SerializedName("data")
    lateinit var data: List<PlatformItem>
}