@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DialogFragment
import android.app.FragmentTransaction
import ru.iqsolution.tkoonline.local.Preferences

@Suppress("MemberVisibilityCanBePrivate")
class DialogManager(activity: Activity) : DialogListener {

    private val fragmentManager = activity.fragmentManager

    private val settingsDialog = SettingsDialog()

    private val passwordDialog = PasswordDialog()

    private var hasPrompted = false

    fun open(preferences: Preferences) {
        transact {
            remove<SettingsDialog>()
            remove<PasswordDialog>()
            if (preferences.enableLock) {
                if (!hasPrompted) {
                    passwordDialog.show(this)
                    return
                }
            }
            settingsDialog.show(this)
        }
    }

    override fun onPrompted() {

    }

    override fun onKioskMode(enter: Boolean) {
        if (enter) {
            hasPrompted = false
        }
    }

    @SuppressLint("CommitTransaction")
    private inline fun transact(action: FragmentTransaction.() -> Unit) {
        fragmentManager.beginTransaction().apply(action)
    }

    private inline fun <reified T> FragmentTransaction.remove() {
        fragmentManager.findFragmentByTag(T::class.java.simpleName)?.let {
            remove(it)
        }
    }

    private fun DialogFragment.show(transaction: FragmentTransaction) {
        show(transaction, dialog.javaClass.simpleName)
    }
}
