package ru.iqsolution.tkoonline.screens.base

import com.google.android.gms.location.LocationSettingsStates

interface IBaseView {

    fun onLocationState(state: LocationSettingsStates?)

    fun showLoading()

    fun hideLoading()
}