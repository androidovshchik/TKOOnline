package ru.iqsolution.tkoonline.services.telemetry

import androidx.annotation.UiThread
import com.chibatching.kotpref.bulk
import ru.iqsolution.tkoonline.local.MemoryStorage
import ru.iqsolution.tkoonline.local.Preferences

class PreferenceHolder : MemoryStorage {

    // Unused
    override var accessToken: String? = null

    // Only read and doesn't update
    override var tokenId = 0L

    @Volatile
    override var mileage = 0f

    @Volatile
    override var packageId = 0

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
                mileage = it.mileage
                packageId = it.packageId
            }
        }
    }
}