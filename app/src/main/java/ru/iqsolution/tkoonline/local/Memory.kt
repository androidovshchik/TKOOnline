package ru.iqsolution.tkoonline.local

/**
 * This class determines which vars and functions of [Preferences] are used in different threads
 */
interface Memory {

    // Logout on background thread
    var token: String?

    // Logout, Read on background thread
    var tokenId: Long

    /**
     * Used only in telemetry
     */
    // Write, Logout, Read on background thread
    var mileage: Float

    /**
     * Used only in telemetry
     */
    // Write, Logout, Read on background thread
    var packageId: Int

    fun logout() {
        mileage = 0f
        packageId = 0
    }
}