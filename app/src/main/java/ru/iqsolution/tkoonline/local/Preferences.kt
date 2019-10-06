package ru.iqsolution.tkoonline.local

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import ru.iqsolution.tkoonline.models.SimpleLocation

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
    // todo elapsed time?
    var blockTime by longPref(0L, "0x08")

    var vehicleNumber by nullableStringPref(null, "0x09")

    /**
     * Boot time at the moment of server time (milliseconds)
     */
    var elapsedTime by longPref(0L, "0x0a")

    var queName by nullableStringPref(null, "0x0b")

    var carId by intPref(0, "0x0c")

    /**
     * Last known latitude
     * NOTICE do not clear it because of photo event coordinates
     */
    var lastLatitude by floatPref(0f, "0x0d")

    /**
     * Last known longitude
     * NOTICE do not clear it because of photo event coordinates
     */
    var lastLongitude by floatPref(0f, "0x0e")

    /**
     * Time of last known location [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    var locationTime by nullableStringPref(null, "0x0f")

    var tokenId by longPref(0L, "0x10")

    var photoCount by intPref(0, "0x11")

    val isLoggedIn: Boolean
        get() = accessToken != null

    val authHeader: String
        get() = "Bearer $accessToken"

    val serverDay: String
        get() = serverTime?.split("T")?.get(0).toString()

    val location: SimpleLocation?
        get() = locationTime?.let {
            SimpleLocation(lastLatitude, lastLongitude)
        }

    /**
     * It's needed to be bulked
     */
    fun logout() {
        accessToken = null
        expiresWhen = null
        allowPhotoRefKp = false
        serverTime = null
        elapsedTime = 0L
        vehicleNumber = null
        queName = null
        carId = 0
        locationTime = null
        tokenId = 0L
    }
}