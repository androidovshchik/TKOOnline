package ru.iqsolution.tkoonline.services

import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.UserManager
import android.provider.Settings
import android.view.View
import org.jetbrains.anko.devicePolicyManager
import ru.iqsolution.tkoonline.receivers.AdminReceiver
import ru.iqsolution.tkoonline.screens.login.LoginActivity

class AdminManager(context: Context) {

    private val componentName: ComponentName = ComponentName(context, AdminReceiver::class.java)

    private val devicePolicyManager = context.devicePolicyManager

    private val packageName = context.packageName

    val isAdministrator: Boolean
        get() = devicePolicyManager.isAdminActive(componentName)

    val isDeviceOwner: Boolean
        get() = devicePolicyManager.isDeviceOwnerApp(packageName)

    private fun setKioskPolicies(enable: Boolean, isAdmin: Boolean) {
        if (isAdmin) {
            setRestrictions(enable)
            enableStayOnWhilePluggedIn(enable)
            setUpdatePolicy(enable)
            setAsHomeApp(enable)
            setKeyGuardEnabled(enable)
        }
        setLockTask(enable, isAdmin)
        setImmersiveMode(enable)
    }

    // region restrictions
    private fun setRestrictions(disallow: Boolean) {
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, disallow)
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, disallow)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, disallow)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, disallow)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, disallow)
    }

    private fun setUserRestriction(restriction: String, disallow: Boolean) = if (disallow) {
        devicePolicyManager.addUserRestriction(AdminReceiver.getComponentName(applicationContext), restriction)
    } else {
        devicePolicyManager.clearUserRestriction(AdminReceiver.getComponentName(applicationContext), restriction)
    }
    // endregion

    private fun enableStayOnWhilePluggedIn(active: Boolean) = if (active) {
        devicePolicyManager.setGlobalSetting(
            AdminReceiver.getComponentName(applicationContext),
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            Integer.toString(
                BatteryManager.BATTERY_PLUGGED_AC
                        or BatteryManager.BATTERY_PLUGGED_USB
                        or BatteryManager.BATTERY_PLUGGED_WIRELESS
            )
        )
    } else {
        devicePolicyManager.setGlobalSetting(
            AdminReceiver.getComponentName(applicationContext),
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            "0"
        )
    }

    private fun setLockTask(start: Boolean, isAdmin: Boolean) {
        if (isAdmin) {
            devicePolicyManager.setLockTaskPackages(
                AdminReceiver.getComponentName(applicationContext),
                if (start) arrayOf(packageName) else arrayOf()
            )
        }
        if (start) {
            startLockTask()
        } else {
            stopLockTask()
        }
    }

    private fun setUpdatePolicy(enable: Boolean) {
        if (enable) {
            devicePolicyManager.setSystemUpdatePolicy(
                AdminReceiver.getComponentName(applicationContext),
                SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
            )
        } else {
            devicePolicyManager.setSystemUpdatePolicy(AdminReceiver.getComponentName(applicationContext), null)
        }
    }

    private fun setAsHomeApp(enable: Boolean) {
        if (enable) {
            val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            devicePolicyManager.addPersistentPreferredActivity(
                AdminReceiver.getComponentName(applicationContext),
                intentFilter,
                ComponentName(packageName, LoginActivity::class.java.name)
            )
        } else {
            devicePolicyManager.clearPackagePersistentPreferredActivities(
                AdminReceiver.getComponentName(applicationContext), packageName
            )
        }
    }

    private fun setKeyGuardEnabled(enable: Boolean) {
        devicePolicyManager.setKeyguardDisabled(AdminReceiver.getComponentName(applicationContext), !enable)
    }

    private fun setImmersiveMode(enable: Boolean) {
        if (enable) {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.decorView.systemUiVisibility = flags
        } else {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.decorView.systemUiVisibility = flags
        }
    }
}
