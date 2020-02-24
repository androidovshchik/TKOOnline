package ru.iqsolution.tkoonline.local

/**
 * This class determines which vars and functions of [Preferences] are used in different threads
 */
// todo comments
interface ThreadStorage {

    // Read on background thread
    var accessToken: String?

    // Logout, Read on background thread
    var tokenId: Long

    // Write, Logout, Read on background thread
    var mileage: Float

    // Write, Logout, Read on background thread
    var packageId: Int

    val isLoggedIn: Boolean
        get() = accessToken != null

    val authHeader: String
        get() = "Bearer $accessToken"

    fun logout() {
        accessToken = null
        tokenId = 0L
        mileage = 0f
        packageId = 0
    }
}