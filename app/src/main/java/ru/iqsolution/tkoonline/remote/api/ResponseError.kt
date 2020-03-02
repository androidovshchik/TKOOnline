package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName

class ServerError {

    @SerializedName("code")
    var code: String? = null

    @SerializedName("description")
    var description: String? = null
}

class ResponseError {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("errors")
    var errors = listOf<ServerError>()
}