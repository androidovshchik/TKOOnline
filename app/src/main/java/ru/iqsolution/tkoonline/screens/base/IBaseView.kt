package ru.iqsolution.tkoonline.screens.base

import org.kodein.di.KodeinAware
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.services.LocationListener

interface IBaseView : KodeinAware, LocationListener {

    fun updateRoute()

    fun onRoute(locationEvents: List<LocationEvent>)

    fun updateCloud()

    fun updateCloud(allCount: Int, photoCount: Int)

    fun onUnhandledError(e: Throwable?)

    fun showError(e: Throwable?)

    fun showError(message: CharSequence?)
}