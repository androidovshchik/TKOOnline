package ru.iqsolution.tkoonline.screens.status

import ru.iqsolution.tkoonline.services.LocationListener

interface SyncListener : LocationListener {

    fun onTimeChanged()

    fun onBatteryChanged(status: Int, level: Int)
}