package ru.iqsolution.tkoonline.services

import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.UserManager
import android.provider.Settings
import org.jetbrains.anko.devicePolicyManager
import ru.iqsolution.tkoonline.receivers.AdminReceiver

@Suppress("MemberVisibilityCanBePrivate")
class AdminManager(context: Context) {

    private val componentName: ComponentName = ComponentName(context, AdminReceiver::class.java)

    private val devicePolicyManager = context.devicePolicyManager

    private val packageName = context.packageName

    val isAdministrator: Boolean
        get() = devicePolicyManager.isAdminActive(componentName)

    val isDeviceOwner: Boolean
        get() = devicePolicyManager.isDeviceOwnerApp(packageName)

    fun setKioskMode(context: Context, enable: Boolean): Boolean {
        if (isDeviceOwner) {
            if (setRestrictions(enable)) {
                if (enableStayOnWhilePluggedIn(enable)) {
                    if (setUpdatePolicy(enable)) {
                        if (setAsHomeApp(enable)) {
                            if (setKeyGuardEnabled(enable)) {
                                if (setLockTask(enable)) {

                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun setRestrictions(disallow: Boolean) {
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, disallow)
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, disallow)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, disallow)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, disallow)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, disallow)
    }

    /**
     * @throws SecurityException if {@code admin} is not a device or profile owner.
     */
    private fun setUserRestriction(restriction: String, disallow: Boolean) = if (disallow) {
        devicePolicyManager.addUserRestriction(componentName, restriction)
    } else {
        devicePolicyManager.clearUserRestriction(componentName, restriction)
    }
    // endregion

    /**
     * @throws SecurityException if {@code admin} is not a device owner.
     */
    private fun enableStayOnWhilePluggedIn(active: Boolean) = if (active) {
        devicePolicyManager.setGlobalSetting(
            componentName,
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            (BatteryManager.BATTERY_PLUGGED_AC
                    or BatteryManager.BATTERY_PLUGGED_USB
                    or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
        )
    } else {
        devicePolicyManager.setGlobalSetting(componentName, Settings.Global.STAY_ON_WHILE_PLUGGED_IN, "0")
    }

    /**
     * @throws SecurityException if {@code admin} is not the device owner, the profile owner of an
     * affiliated user or profile, or the profile owner when no device owner is set.
     */
    private fun setLockTask(start: Boolean, isAdmin: Boolean) {
        if (isAdmin) {
            devicePolicyManager.setLockTaskPackages(componentName, if (start) arrayOf(packageName) else arrayOf())
        }
        if (start) {
            startLockTask()
        } else {
            stopLockTask()
        }
    }

    /**
     * @throws SecurityException if {@code admin} is not a device owner.
     */
    private fun setUpdatePolicy(enable: Boolean) {
        if (enable) {
            devicePolicyManager.setSystemUpdatePolicy(
                componentName,
                SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
            )
        } else {
            devicePolicyManager.setSystemUpdatePolicy(componentName, null)
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
            devicePolicyManager.addPersistentPreferredActivity(
                componentName, intentFilter, ComponentName(packageName, MainActivity::class.java.name)
            )
        } else {
            devicePolicyManager.clearPackagePersistentPreferredActivities(
                componentName, packageName
            )
        }
    }

    /**
     * @throws SecurityException if {@code admin} is not the device owner, or a profile owner of
     * secondary user that is affiliated with the device.
     */
    private fun setKeyGuardEnabled(enable: Boolean) {
        devicePolicyManager.setKeyguardDisabled(componentName, !enable)
    }
}
