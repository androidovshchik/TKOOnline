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
     * In milliseconds and UTC
     */
    var expiresToken by longPref(0L, "expiresToken")

    var allowPhotoRefKp by booleanPref(false, "allowPhotoRefKp")
}