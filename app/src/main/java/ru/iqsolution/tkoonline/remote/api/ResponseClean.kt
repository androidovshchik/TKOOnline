package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName

class ResponseClean : RequestClean() {

    @SerializedName("id")
    lateinit var id: String
}