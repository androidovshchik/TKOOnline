package ru.iqsolution.tkoonline.screens.map

interface MapListener {

    fun onReady()

    fun onPlatform(kpId: Int)
}