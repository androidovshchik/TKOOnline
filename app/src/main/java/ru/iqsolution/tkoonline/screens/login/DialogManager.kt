@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.FragmentTransaction
import ru.iqsolution.tkoonline.local.Preferences

@Suppress("MemberVisibilityCanBePrivate")
class DialogManager(activity: Activity) : DialogCallback {

    private val fragmentManager = activity.fragmentManager

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    override fun openSettings(preferences: Preferences) {
        transact {
            remove(passwordDialog)
            remove(settingsDialog)
            if (preferences.enableLock) {
                if (!hasPrompted) {
                    passwordDialog.show(this, passwordDialog.javaClass.simpleName)
                    return
                }
            }
            settingsDialog.show(this, settingsDialog.javaClass.simpleName)
        }
    }

    override fun setupPassword() {
        transact {
            remove(passwordDialog)
            passwordDialog.show(this, passwordDialog.javaClass.simpleName)
        }
    }

    override fun enterKioskMode() {
        hasPrompted = false
    }

    override fun exitKioskMode() {}

    @SuppressLint("CommitTransaction")
    private inline fun transact(action: FragmentTransaction.() -> Unit) {
        fragmentManager.beginTransaction().apply(action)
    }
}
