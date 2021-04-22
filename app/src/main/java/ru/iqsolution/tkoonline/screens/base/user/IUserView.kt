package ru.iqsolution.tkoonline.screens.base.user

import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.screens.base.IBaseView
import ru.iqsolution.tkoonline.telemetry.LocationListener

interface IUserView : IBaseView, LocationListener {

    fun updateRoute()

    fun onRoute(locationEvents: List<LocationEvent>) {
    }

    fun updateCloud()

    fun onCloudUpdate(allCount: Int, photoCount: Int)
}