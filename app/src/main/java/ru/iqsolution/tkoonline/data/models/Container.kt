package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

open class Container {

    @SerializedName("container_type")
    lateinit var containerType: ContainerType

    @SerializedName("container_type_volume")
    var containerVolume = 0f

    @SerializedName("container_count")
    var containerCount = 0

    val isEmpty: Boolean
        get() = containerVolume < 0.1f

    constructor()

    constructor(type: ContainerType) {
        containerType = type
    }

    fun addFrom(container: Container) {
        if (isEmpty) {
            containerVolume = container.containerVolume
        }
        containerCount += container.containerCount
    }
}