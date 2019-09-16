package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class ResponseContainers {

    @SerializedName("data")
    lateinit var data: List<ContainerInfo>
}