package ru.iqsolution.tkoonline.screens.base

import ru.iqsolution.tkoonline.local.entities.LocationEvent

interface IBaseView {

    fun updateRoute()

    fun onRoute(locationEvents: List<LocationEvent>)

    fun updateCloud()

    fun updateCloud(allCount: Int, photoCount: Int)

    fun showError(e: Throwable?)

    fun showError(message: CharSequence?)
}