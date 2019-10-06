package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

/**
 * Special class for linked platforms
 */
class SimpleContainer(type: ContainerType) : Container {

    @SerializedName("l")
    var linkedIds = arrayListOf<Int>()

    @SerializedName("t")
    override var containerType: String = type.id

    @SerializedName("v")
    override var containerVolume = 0f

    @SerializedName("c")
    override var containerCount = 0
}