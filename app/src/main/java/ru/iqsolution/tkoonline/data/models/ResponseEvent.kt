package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class ResponseEvent : RequestEvent() {

    @SerializedName("id")
    lateinit var id: String
}