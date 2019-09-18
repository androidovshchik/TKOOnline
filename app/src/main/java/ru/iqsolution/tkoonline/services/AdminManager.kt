package ru.iqsolution.tkoonline.services

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.UserManager
import org.jetbrains.anko.devicePolicyManager
import ru.iqsolution.tkoonline.receivers.AdminReceiver
import ru.iqsolution.tkoonline.screens.login.LoginActivity

@Suppress("MemberVisibilityCanBePrivate")
class AdminManager(context: Context) {

    private val componentName: ComponentName = ComponentName(context, AdminReceiver::class.java)

    private val devicePolicyManager = context.devicePolicyManager

    private val packageName = context.packageName

    @Suppress("unused")
    val isAdmin: Boolean
        get() = devicePolicyManager.isAdminActive(componentName)

    val isDeviceOwner: Boolean
        get() = devicePolicyManager.isDeviceOwnerApp(packageName)

    fun setKioskMode(activity: Activity, enable: Boolean): Boolean {
        // todo
        //setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, disallow)
        //setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, disallow)
        //devicePolicyManager.setSystemUpdatePolicy(componentName, null)
        //devicePolicyManager.setKeyguardDisabled(componentName, !enable)
        if (isDeviceOwner) {
            setRestrictions(enable)
            setAsHomeApp(enable)
            setLockTask(activity, enable)
            return true
        }
        return false
    }

    /**
     * @throws SecurityException if {@code admin} is not a device or profile owner.
     */
    private fun setRestrictions(disallow: Boolean) {
        arrayOf(
            UserManager.DISALLOW_USER_SWITCH,
            UserManager.DISALLOW_FACTORY_RESET,
            UserManager.DISALLOW_SAFE_BOOT,
            UserManager.DISALLOW_ADD_USER,
            UserManager.DISALLOW_APPS_CONTROL,
            UserManager.DISALLOW_UNINSTALL_APPS,
            UserManager.DISALLOW_CREATE_WINDOWS,
            UserManager.DISALLOW_SYSTEM_ERROR_DIALOGS,
            UserManager.DISALLOW_DEBUGGING_FEATURES
        ).forEach {
            if (disallow) {
                devicePolicyManager.addUserRestriction(componentName, it)
            } else {
                devicePolicyManager.clearUserRestriction(componentName, it)
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
            devicePolicyManager.addPersistentPreferredActivity(
                componentName, intentFilter,
                ComponentName(packageName, LoginActivity::class.java.name)
            )
        } else {
            devicePolicyManager.clearPackagePersistentPreferredActivities(componentName, packageName)
        }
    }

    /**
     * @throws SecurityException if {@code admin} is not the device owner, the profile owner of an
     * affiliated user or profile, or the profile owner when no device owner is set.
     */
    private fun setLockTask(activity: Activity, enable: Boolean) = activity.run {
        if (enable) {
            devicePolicyManager.setLockTaskPackages(componentName, arrayOf(packageName))
            startLockTask()
        } else {
            devicePolicyManager.setLockTaskPackages(componentName, arrayOf())
            stopLockTask()
        }
    }
}
