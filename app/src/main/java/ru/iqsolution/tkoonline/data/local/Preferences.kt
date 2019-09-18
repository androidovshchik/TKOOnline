/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

package ru.iqsolution.tkoonline.data.local

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class Preferences(context: Context) : KotprefModel(context) {

    override val kotprefName: String = "preferences"

    var accessToken by nullableStringPref(null, "0x00")

    /**
     * UTC (format)
     */
    var expiresToken by nullableStringPref(null, "0x01")

    var allowPhotoRefKp by booleanPref(false, "0x02")

    /**
     * Server time UTC (format)
     */
    var serverTime by nullableStringPref(null, "#000000")

    var mainServerAddress by stringPref("msk-mob.iqsolution.ru:7778", "0x04")

    var mainTelemetryAddress by stringPref("msk-mob.iqsolution.ru:7779", "0x05")

    var enableLock by booleanPref(false, "0x06")

    var lockPassword by nullableStringPref(null, "0x07")

    /**
     * Local time (milliseconds)
     */
    var blockTime by longPref(0L, "0x08")

    val isLoggedIn: Boolean
        get() = accessToken != null

    val authHeader: String
        get() = "Bearer $accessToken"
}