package ru.iqsolution.tkoonline.screens.login

interface DialogListener {

    /**
     * Called from [SettingsDialog]
     */
    fun onKioskMode(enter: Boolean)
}