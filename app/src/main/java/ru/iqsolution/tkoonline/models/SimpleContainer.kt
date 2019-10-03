package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

/**
 * Special class for linked platforms to summarize them
 */
class SimpleContainer : Container {

    @SerializedName("l")
    var linkedIds = arrayListOf<Int>()

    @SerializedName("t")
    override lateinit var containerType: String

    @SerializedName("v")
    override var containerVolume = 0f

    @SerializedName("c")
    override var containerCount = 0
}