package ru.iqsolution.tkoonline.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.UserManager
import org.jetbrains.anko.devicePolicyManager
import ru.iqsolution.tkoonline.receivers.AdminReceiver
import ru.iqsolution.tkoonline.screens.LockActivity

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
            setAsHomeApp(enable)
            setKeyGuardEnabled(enable)
            setLockTask(enable)
            return true
        }
        return false
    }

    /**
     * @throws SecurityException if {@code admin} is not a device or profile owner.
     */
    private fun setRestrictions(disallow: Boolean) {
        arrayOf(
            UserManager.DISALLOW_FACTORY_RESET,
            UserManager.DISALLOW_SAFE_BOOT,
            UserManager.DISALLOW_USER_SWITCH,
            UserManager.DISALLOW_ADD_USER
        ).forEach {
            if (disallow) {
                deviceManager.addUserRestriction(adminComponent, it)
            } else {
                deviceManager.clearUserRestriction(adminComponent, it)
            }
        }
    }

    /**
     * @throws SecurityException if {@code admin} is not a device or profile owner.
     */
    private fun setAsHomeApp(enable: Boolean) {
        if (enable) {
            val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            val activityComponent = ComponentName(packageName, LockActivity::class.java.name)
            deviceManager.addPersistentPreferredActivity(adminComponent, intentFilter, activityComponent)
        } else {
            deviceManager.clearPackagePersistentPreferredActivities(adminComponent, packageName)
        }
    }

    /**
     * @throws SecurityException if {@code admin} is not the device owner, or a profile owner of
     * secondary user that is affiliated with the device.
     */
    private fun setKeyGuardEnabled(enable: Boolean) {
        deviceManager.setKeyguardDisabled(adminComponent, !enable)
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
