package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName

class ServerError {

    @SerializedName("code")
    lateinit var code: String

    @SerializedName("description")
    lateinit var description: String
}

class ResponseError {

    @SerializedName("status")
    lateinit var status: String

    @SerializedName("errors")
    lateinit var errors: List<ServerError>
}