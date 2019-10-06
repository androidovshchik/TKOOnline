package ru.iqsolution.tkoonline.screens.login

interface SettingsListener {

    fun openDialog()

    fun openSettingsDialog()

    fun openPasswordDialog()

    fun enterKioskMode()

    fun exitKioskMode()
}