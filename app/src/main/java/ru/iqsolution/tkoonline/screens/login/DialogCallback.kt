package ru.iqsolution.tkoonline.screens.login

import ru.iqsolution.tkoonline.local.Preferences

interface DialogCallback {

    fun openSettings(preferences: Preferences)

    /**
     * Open [PasswordDialog] to save password
     */
    fun setupPassword()

    fun enterKioskMode()

    fun exitKioskMode()
}