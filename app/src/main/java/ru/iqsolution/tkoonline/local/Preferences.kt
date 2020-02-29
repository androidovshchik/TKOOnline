package ru.iqsolution.tkoonline.local

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.PASSWORD_RETRY
import ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.models.Location
import ru.iqsolution.tkoonline.models.SimpleLocation

class Preferences(context: Context) : KotprefModel(context), Memory, Location<Float> {

    override val kotprefName: String = "preferences"

    // Logout on background thread
    override var accessToken by nullableStringPref(null, "0x00")

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE]
     * Logout on background thread
     */
    var expiresWhen by nullableStringPref(null, "0x01")

    // Logout on background thread
    var allowPhotoRefKp by booleanPref(false, "0x02")

    /**
     * Server time [ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE]
     * Logout on background thread
     */
    var serverTime by nullableStringPref(null, "0x03")

    var mainServerAddress by stringPref("${if (BuildConfig.PROD) "msknt" else "msknt2"}.iqsolution.ru", "0x04")

    var mainTelemetryAddress by stringPref("${if (BuildConfig.PROD) "msknt" else "msknt2"}.iqsolution.ru:5672", "0x05")

    var enableLock by booleanPref(false, "0x06")

    /**
     * Only numbers
     */
    var lockPassword by nullableStringPref(null, "0x07")

    /**
     * Boot time (milliseconds)
     */
    var blockTime by longPref(-PASSWORD_RETRY, "0x08")

    // Logout on background thread
    var vehicleNumber by nullableStringPref(null, "0x09")

    /**
     * Boot time synced with server (milliseconds)
     * Logout on background thread
     */
    //var elapsedTime by longPref(0L, "0x0a")

    // Logout on background thread
    var queName by nullableStringPref(null, "0x0b")

    // Logout on background thread
    var carId by intPref(0, "0x0c")

    /**
     * Last known latitude
     * NOTICE do not clear it because of photo event coordinates
     */
    override var latitude by floatPref(0f, "0x0d")

    /**
     * Last known longitude
     * NOTICE do not clear it because of photo event coordinates
     */
    override var longitude by floatPref(0f, "0x0e")

    /**
     * Time of last known location [ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE]
     * Logout on background thread
     */
    var locationTime by nullableStringPref(null, "0x0f")

    override var tokenId by longPref(0L, "0x10")

    override var mileage by floatPref(0f, "0x11")

    override var packageId by intPref(0, "0x12")

    var enableLogs by booleanPref(!BuildConfig.PROD && !BuildConfig.DEBUG, "0x13")

    var showRoute by booleanPref(false, "0x14")

    // Logout on background thread
    var enableLight by booleanPref(false, "0x15")

    val isLoggedIn: Boolean
        get() = try {
            check(accessToken != null)
            expiresWhen.let {
                check(it != null)
                val now = DateTime.now()
                check(DateTime.parse(it, PATTERN_DATETIME_ZONE).withZone(now.zone).millis >= now.millis)
            }
            check(serverTime != null)
            //check(elapsedTime > 0L)
            check(vehicleNumber != null)
            check(queName != null)
            check(carId > 0)
            check(tokenId > 0L)
            true
        } catch (e: Throwable) {
            false
        }

    val serverDay: String
        get() = serverTime?.substringBefore("T").toString()

    val location: SimpleLocation?
        get() = locationTime?.let {
            SimpleLocation(latitude, longitude)
        }

    /**
     * It's needed to be bulked
     */
    override fun logout() {
        super.logout()
        accessToken = null
        expiresWhen = null
        allowPhotoRefKp = false
        serverTime = null
        //elapsedTime = 0L
        vehicleNumber = null
        queName = null
        carId = 0
        locationTime = null
        tokenId = 0L
        enableLight = false
    }
}