package ru.iqsolution.tkoonline.telemetry

import androidx.annotation.UiThread
import com.chibatching.kotpref.bulk
import ru.iqsolution.tkoonline.local.Memory
import ru.iqsolution.tkoonline.local.Preferences

class PreferenceHolder : Memory {

    // Unused
    override var token: String? = null

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
        val my = this
        preferences.bulk {
            mileage = my.mileage
            packageId = my.packageId
        }
    }
}