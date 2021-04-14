package ru.iqsolution.tkoonline.screens.base

import org.kodein.di.DIAware
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.telemetry.LocationListener

interface IBaseView : DIAware, LocationListener {

    val isTouchable: Boolean

    fun setTouchable(enable: Boolean)

    fun updateRoute()

    fun onRoute(locationEvents: List<LocationEvent>)

    fun updateCloud()

    fun updateCloud(allCount: Int, photoCount: Int)

    fun onUnhandledError(e: Throwable?)

    fun showError(e: Throwable?)

    fun showError(message: CharSequence?)
}