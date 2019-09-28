package ru.iqsolution.tkoonline.screens.login

import android.app.Activity

@Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")
class DialogManager(activity: Activity) {

    private val fragmentManager = activity.fragmentManager

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    fun sssfsf() {
        fragmentManager.beginTransaction().apply {
            fragmentManager.findFragmentByTag(settingsDialog.javaClass.simpleName)?.let {
                remove(it)
            }
            fragmentManager.findFragmentByTag(passwordDialog.javaClass.simpleName)?.let {
                remove(it)
            }
            if (hasPrompted) {
                settingsDialog.show(this, settingsDialog.javaClass.simpleName)
            } else {
                passwordDialog.show(this, passwordDialog.javaClass.simpleName)
            }
        }
    }

    fun onSuccessPrompt() {
        hasPrompted = true
        fragmentManager.beginTransaction().apply {
            fragmentManager.findFragmentByTag(passwordDialog.javaClass.simpleName)?.let {
                remove(it)
            }
            settingsDialog.show(this, settingsDialog.javaClass.simpleName)
        }
    }
}
