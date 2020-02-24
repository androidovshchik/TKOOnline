package ru.iqsolution.tkoonline.services.telemetry

import androidx.annotation.UiThread
import com.chibatching.kotpref.bulk
import ru.iqsolution.tkoonline.local.Preferences

class PreferenceHolder {

    @Volatile
    private var tokenId = 0L

    @Volatile
    private var mileage = 0f

    @Volatile
    private var packageId = 0

    @UiThread
    fun init(preferences: Preferences) {
        tokenId = preferences.tokenId
        mileage = preferences.mileage
        packageId = preferences.packageId
    }

    @UiThread
    fun save(preferences: Preferences) {
        let {
            preferences.bulk {
                tokenId = it.tokenId
                mileage = it.mileage
                packageId = it.packageId
            }
        }
    }
}