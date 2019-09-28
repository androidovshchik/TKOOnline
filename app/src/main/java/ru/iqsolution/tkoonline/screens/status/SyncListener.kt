package ru.iqsolution.tkoonline.screens.status

import ru.iqsolution.tkoonline.services.LocationListener

interface SyncListener : LocationListener {

    fun updateTime()

    fun updateLocation(available: Boolean)

    fun updateConnection(icon: Int)

    fun updateBattery(status: Int, level: Int)
}