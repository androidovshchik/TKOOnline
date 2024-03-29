package ru.iqsolution.tkoonline.screens.common.status

import ru.iqsolution.tkoonline.telemetry.LocationListener

interface StatusListener : LocationListener {

    fun onTimeChanged()

    fun onNfcChanged(available: Boolean)

    /**
     * NOTICE may be called on background thread
     */
    fun onNetworkChanged(available: Boolean)

    fun onBatteryChanged(status: Int, level: Int)

    /**
     * Requires to find out new data
     */
    fun onLocationEvent()

    /**
     * Requires to find out new data
     */
    fun onCloudChanged()

    fun onCloudChanged(hasData: Boolean, photoCount: Int)
}