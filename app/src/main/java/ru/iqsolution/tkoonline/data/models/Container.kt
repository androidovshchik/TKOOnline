package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

open class Container {

    @SerializedName("container_type")
    lateinit var containerType: ContainerType

    @SerializedName("container_type_volume")
    var containerTypeVolume = 0f

    @SerializedName("container_count")
    var containerCount = 0

    constructor()

    constructor(type: ContainerType) {
        containerType = type
    }
}