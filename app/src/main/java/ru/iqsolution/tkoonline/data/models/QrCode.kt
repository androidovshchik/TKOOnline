package ru.iqsolution.tkoonline.data.models

import com.google.gson.annotations.SerializedName

class QrCode {

    @SerializedName("car_id")
    var carId: Int? = null

    @SerializedName("pass")
    lateinit var pass: String

    @SerializedName("reg_num")
    lateinit var regNum: String
}