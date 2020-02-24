package ru.iqsolution.tkoonline.services.telemetry

import androidx.annotation.UiThread
import com.chibatching.kotpref.bulk
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.local.ThreadStorage

class PreferenceHolder : ThreadStorage {

    @Volatile
    override var accessToken: String? = null

    @Volatile
    override var tokenId = 0L

    @Volatile
    override var mileage = 0f

    @Volatile
    override var packageId = 0

    @UiThread
    fun init(preferences: Preferences) {
        accessToken = preferences.accessToken
        tokenId = preferences.tokenId
        mileage = preferences.mileage
        packageId = preferences.packageId
    }

    @UiThread
    fun save(preferences: Preferences) {
        let {
            preferences.bulk {
                accessToken = it.accessToken
                tokenId = it.tokenId
                mileage = it.mileage
                packageId = it.packageId
            }
        }
    }
}