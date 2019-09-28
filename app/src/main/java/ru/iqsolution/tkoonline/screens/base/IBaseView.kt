package ru.iqsolution.tkoonline.screens.base

import com.google.android.gms.location.LocationSettingsStates
import ru.iqsolution.tkoonline.services.LocationListener

interface IBaseView : LocationListener {

    /**
     * NOTICE will be called from [ru.iqsolution.tkoonline.screens.status.StatusFragment]
     * @param state null if location settings are not satisfied
     */
    fun onLocationState(state: LocationSettingsStates?)

    fun showLoading()

    fun hideLoading()
}