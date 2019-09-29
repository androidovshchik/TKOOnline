@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.FragmentManager
import ru.iqsolution.tkoonline.local.Preferences

@Suppress("MemberVisibilityCanBePrivate")
class DialogManager(activity: Activity) : LoginContract.View {

    private val fragmentManager = activity.fragmentManager

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    fun open(preferences: Preferences) {

    }

    override fun onPrompted() {

    }

    override fun onKioskMode(enter: Boolean) {
        if (enter) {
            hasPrompted = false
        }
    }

    override fun onAuthorized() {}

    @SuppressLint("CommitTransaction")
    private inline fun transact(commit: Boolean = false, action: FragmentManager.() -> Unit) {
        fragmentManager.apply {
            beginTransaction().apply {
                action()
                if (commit) {
                    commit()
                }
            }
        }
    }
}
