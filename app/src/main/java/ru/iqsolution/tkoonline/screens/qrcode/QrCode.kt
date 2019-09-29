package ru.iqsolution.tkoonline.screens.qrcode

import com.google.gson.annotations.SerializedName

class QrCode {

    @SerializedName("car_id")
    var carId = 0

    @SerializedName("pass")
    lateinit var pass: String

    @SerializedName("reg_num")
    lateinit var regNum: String
}