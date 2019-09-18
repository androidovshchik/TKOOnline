package ru.iqsolution.tkoonline.services

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.UserManager
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.devicePolicyManager
import org.jetbrains.anko.toast
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

    fun setKioskMode(activity: Activity, enable: Boolean) {
        if (isDeviceOwner) {
            setRestrictions(enable)
            setAsHomeApp(activity, enable)
            setKeyGuardEnabled(enable)
            setLockTask(activity, enable)
        } else {
            activity.toast("Требуются права владельца устройства")
        }
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
    private fun setAsHomeApp(activity: Activity, enable: Boolean) {
        if (enable) {
            val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            deviceManager.addPersistentPreferredActivity(adminComponent, intentFilter, activity.componentName)
        } else {
            deviceManager.clearPackagePersistentPreferredActivities(adminComponent, packageName)
        }
    }

    private fun setKeyGuardEnabled(enable: Boolean) {
        deviceManager.setKeyguardDisabled(adminComponent, !enable)
    }

    /**
     * @throws SecurityException if {@code admin} is not the device owner, the profile owner of an
     * affiliated user or profile, or the profile owner when no device owner is set.
     */
    private fun setLockTask(activity: Activity, enable: Boolean) = activity.run {
        if (enable) {
            devicePolicyManager.setLockTaskPackages(adminComponent, arrayOf(packageName))
            startLockTask()
        } else {
            devicePolicyManager.setLockTaskPackages(adminComponent, arrayOf())
            stopLockTask()
            startActivity(
                Intent(applicationContext, javaClass)
                    .clearTop()
            )
        }
    }
}
