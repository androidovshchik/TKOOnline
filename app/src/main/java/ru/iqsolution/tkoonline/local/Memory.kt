package ru.iqsolution.tkoonline.local

/**
 * This class determines which vars and functions of [Preferences] are used in different threads
 */
interface Memory {

    // Logout on background thread
    var accessToken: String?

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

    // Read on background thread
    val authHeader: String?
        get() = if (accessToken != null) "Bearer $accessToken" else null

    fun logout() {
        mileage = 0f
        packageId = 0
    }
}