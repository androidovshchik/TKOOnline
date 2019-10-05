package ru.iqsolution.tkoonline.screens.platforms

import ru.iqsolution.tkoonline.local.Preferences

interface WaitListener {

    fun openDialog(preferences: Preferences)

    fun openSettingsDialog()

    fun openPasswordDialog()

    fun enterKioskMode()

    fun exitKioskMode()
}