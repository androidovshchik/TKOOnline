package ru.iqsolution.tkoonline

import android.content.ComponentName
import android.content.Context
import android.os.UserManager
import org.jetbrains.anko.devicePolicyManager
import ru.iqsolution.tkoonline.receivers.AdminReceiver

@Suppress("MemberVisibilityCanBePrivate")
class AdminManager(context: Context) {

    private val adminComponent = ComponentName(context, AdminReceiver::class.java)

    private val deviceManager = context.devicePolicyManager

    private val packageName = context.packageName

    @Suppress("unused")
    val isAdmin: Boolean
        get() = deviceManager.isAdminActive(adminComponent)

    val isDeviceOwner: Boolean
        get() = deviceManager.isDeviceOwnerApp(packageName)

    fun setKioskMode(enable: Boolean): Boolean {
        if (isDeviceOwner) {
            setRestrictions(enable)
            deviceManager.setKeyguardDisabled(adminComponent, enable)
            setLockTask(enable)
            return true
        }
        return false
    }

    /**
     * @throws SecurityException if {@code admin} is not a device or profile owner.
     */
    private fun setRestrictions(enable: Boolean) {
        arrayOf(
            UserManager.DISALLOW_FACTORY_RESET,
            UserManager.DISALLOW_SAFE_BOOT,
            UserManager.DISALLOW_ADD_USER
        ).forEach {
            if (enable) {
                deviceManager.addUserRestriction(adminComponent, it)
            } else {
                deviceManager.clearUserRestriction(adminComponent, it)
            }
        }
    }

    /**
     * @throws SecurityException if {@code admin} is not the device owner, the profile owner of an
     * affiliated user or profile, or the profile owner when no device owner is set.
     */
    private fun setLockTask(enable: Boolean) {
        if (enable) {
            deviceManager.setLockTaskPackages(adminComponent, arrayOf(packageName))
        } else {
            deviceManager.setLockTaskPackages(adminComponent, arrayOf())
        }
    }
}
