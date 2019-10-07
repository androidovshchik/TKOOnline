package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Special class for linked platforms
 */
class SimpleContainer(type: ContainerType) : Serializable, Container {

    @SerializedName("l")
    var linkedIds = arrayListOf<Int>()

    @SerializedName("t")
    override var containerType: String = type.id

    @SerializedName("v")
    override var containerVolume = 0f

    @SerializedName("c")
    override var containerCount = 0
}