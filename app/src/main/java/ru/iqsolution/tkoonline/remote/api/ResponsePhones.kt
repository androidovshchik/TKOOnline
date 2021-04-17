package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.Contact

class ResponsePhones {

    @SerializedName("data")
    lateinit var data: List<Contact>
}