package ru.iqsolution.tkoonline.local

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class Preferences(context: Context) : KotprefModel(context) {

    override val kotprefName: String = "preferences"

    var accessToken by nullableStringPref(null, "0x00")

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    var expiresWhen by nullableStringPref(null, "0x01")

    var allowPhotoRefKp by booleanPref(false, "0x02")

    /**
     * Server time [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    var serverTime by nullableStringPref(null, "0x03")

    var mainServerAddress by stringPref("msk-mob.iqsolution.ru:7778", "0x04")

    var mainTelemetryAddress by stringPref("msk-mob.iqsolution.ru:7779", "0x05")

    var enableLock by booleanPref(false, "0x06")

    var lockPassword by nullableStringPref(null, "0x07")

    /**
     * Local time (milliseconds)
     */
    var blockTime by longPref(0L, "0x08")

    var vehicleNumber by nullableStringPref(null, "0x09")

    /**
     * At the moment of server time (milliseconds)
     */
    var elapsedTime by longPref(0L, "0x0a")

    var queName by nullableStringPref(null, "0x0b")

    var carId by intPref(0, "0x0c")

    var latitude by floatPref(Float.MAX_VALUE, "0x0d")

    var longitude by floatPref(Float.MAX_VALUE, "0x0e")

    /**
     * !!! Non properties below
     */

    val isLoggedIn: Boolean
        get() = accessToken != null

    val authHeader: String
        get() = "Bearer $accessToken"

    val serverDay: String
        get() = serverTime?.split("T")?.get(0).toString()
}