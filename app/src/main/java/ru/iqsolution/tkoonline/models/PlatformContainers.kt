package ru.iqsolution.tkoonline.models

import com.google.gson.annotations.SerializedName

/**
 * Special class for non-linked platforms
 */
class PlatformContainers : Platform() {

    var regular = SimpleContainer()

    var bunker = SimpleContainer()

    var bunk = SimpleContainer()

    var special = SimpleContainer()

    var unknown = SimpleContainer()

    /**
     * It's needed only for map
     */
    @SerializedName("p_errors")
    var errors: String? = null
}