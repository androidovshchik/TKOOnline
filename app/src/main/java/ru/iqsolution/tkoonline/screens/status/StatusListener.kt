package ru.iqsolution.tkoonline.screens.status

import android.app.Activity

interface StatusListener {

    fun getActivity(): Activity?

    fun updateTime()

    fun updateLocation(available: Boolean)

    fun updateConnection(icon: Int)

    fun updateBattery(status: Int, level: Int)
}