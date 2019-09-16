/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

package ru.iqsolution.tkoonline.data.local

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class Preferences(context: Context) : KotprefModel(context) {

    override val kotprefName: String = "preferences"

    var accessToken by nullableStringPref(null, "accessToken")

    /**
     * UTC. In milliseconds
     */
    var expiresToken by longPref(0L, "expiresToken")

    var allowPhotoRefKp by booleanPref(false, "allowPhotoRefKp")

    /**
     * In milliseconds
     */
    var serverTimeDifference by longPref(0L, "serverTimeDifference")

    var mainServerAddress by stringPref("msk-mob.iqsolution.ru:7778", "mainServerAddress")

    var mainTelemetryAddress by stringPref("msk-mob.iqsolution.ru:7779", "mainServerAddress")

    var enableLock by booleanPref(false, "enableLock")

    var lockPassword by nullableStringPref(null, "lockPassword")

    /**
     * For password to enter lock settings. UTC. In milliseconds
     */
    var nextAttemptsAfter by longPref(0L, "nextAttemptsAfter")

    val isLoggedIn: Boolean
        get() = accessToken != null
}