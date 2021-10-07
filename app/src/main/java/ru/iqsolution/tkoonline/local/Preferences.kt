package ru.iqsolution.tkoonline.local

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.models.Location
import ru.iqsolution.tkoonline.models.SimpleLocation
import timber.log.Timber
import java.time.LocalDate
import java.time.ZonedDateTime

class Preferences(context: Context) : KotprefModel(context), Memory, Location<Float> {

    override val kotprefName: String = "preferences"

    // Logout on background thread
    override var token by nullableStringPref(null, "0x00")

    /**
     * Logout on background thread
     */
    @Pattern(Pattern.DATETIME_ZONE)
    var expiresWhen by nullableStringPref(null, "0x01")

    // Logout on background thread
    var allowCustomTasks by booleanPref(false, "0x02")

    /**
     * Logout on background thread
     */
    @Pattern(Pattern.DATETIME_ZONE)
    var serverTime by nullableStringPref(null, "0x03")

    var mainServerAddress by stringPref("${if (BuildConfig.PROD) "mkm" else "msknt2"}.iqsolution.ru", "0x04")

    var mainTelemetryAddress by stringPref("${if (BuildConfig.PROD) "mkm" else "msknt2"}.iqsolution.ru:5672", "0x05")

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
     * Time of last known location
     * Logout on background thread
     */
    @Pattern(Pattern.DATETIME_ZONE)
    var locationTime by nullableStringPref(null, "0x0f")

    override var tokenId by longPref(0L, "0x10")

    override var mileage by floatPref(0f, "0x11")

    override var packageId by intPref(0, "0x12")

    var enableLogs by booleanPref(!BuildConfig.PROD && !BuildConfig.DEBUG, "0x13")

    var showRoute by booleanPref(false, "0x14")

    // Logout on background thread
    var enableLight by booleanPref(false, "0x15")

    // on exit unexpectedly
    // Logout on background thread
    var invalidAuth by booleanPref(false, "0x16")

    var useSpeaker by booleanPref(false, "0x17")

    val isLoggedIn: Boolean
        get() = try {
            check(!invalidAuth) { "invalidAuth" }
            check(!token.isNullOrBlank()) { "accessToken == null" }
            expiresWhen.let {
                check(it != null) { "expiresWhen == null" }
                check(ZonedDateTime.parse(it, patternDateTimeZone).isLater()) { "expiresWhen <= now" }
            }
            check(!serverTime.isNullOrBlank()) { "serverTime == null" }
            check(vehicleNumber != null) { "vehicleNumber == null" }
            check(!queName.isNullOrBlank()) { "queName == null" }
            check(carId > 0) { "carId <= 0" }
            check(tokenId > 0L) { "tokenId <= 0L" }
            true
        } catch (e: Throwable) {
            Timber.e("isLoggedIn: ${e.message}")
            false
        }

    val serverDay: LocalDate
        get() {
            return ZonedDateTime.parse(serverTime!!, patternDateTimeZone)
                .withZoneSameInstant(midnightZone)
                .toLocalDate()
        }

    val location: SimpleLocation?
        get() = locationTime?.let {
            SimpleLocation(latitude, longitude)
        }

    /**
     * It's needed to be bulked
     */
    override fun logout() {
        super.logout()
        token = null
        expiresWhen = null
        allowCustomTasks = false
        serverTime = null
        //elapsedTime = 0L
        vehicleNumber = null
        queName = null
        carId = 0
        locationTime = null
        tokenId = 0L
        enableLight = false
        invalidAuth = false
    }
}