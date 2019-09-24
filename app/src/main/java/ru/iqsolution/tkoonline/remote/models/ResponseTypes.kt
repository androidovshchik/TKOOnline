package ru.iqsolution.tkoonline.remote.models

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.PhotoItem

class ResponseTypes {

    @SerializedName("data")
    lateinit var data: List<PhotoItem>
}