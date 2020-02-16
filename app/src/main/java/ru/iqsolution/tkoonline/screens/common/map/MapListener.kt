package ru.iqsolution.tkoonline.screens.common.map

interface MapListener {

    fun onReady()

    fun onPlatform(kpId: Int)
}