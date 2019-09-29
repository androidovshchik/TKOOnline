package ru.iqsolution.tkoonline.screens.status

import ru.iqsolution.tkoonline.services.LocationListener

interface SyncListener : LocationListener {

    fun onTimeChanged()

    /**
     * NOTICE may be called on background thread
     */
    fun onNetworkChanged(available: Boolean)

    fun onBatteryChanged(status: Int, level: Int)
}