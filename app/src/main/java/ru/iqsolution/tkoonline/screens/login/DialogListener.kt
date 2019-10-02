package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.local.Preferences

interface DialogListener {

    fun openDialog(preferences: Preferences)

    fun openSettingsDialog()

    fun openPasswordDialog()

    fun enterKioskMode()

    fun exitKioskMode()
}