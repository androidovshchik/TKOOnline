package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

/**
 * Special class for non-linked platforms
 */
class PlatformContainers : Platform() {

    @SerializedName("_r")
    var regular = SimpleContainer()

    @SerializedName("_br")
    var bunker = SimpleContainer()

    @SerializedName("_b")
    var bunk = SimpleContainer()

    @SerializedName("_s")
    var special = SimpleContainer()

    @SerializedName("_u")
    var unknown = SimpleContainer()

    /**
     * It's needed only for map
     */
    @SerializedName("_e")
    var errors: String? = null
}