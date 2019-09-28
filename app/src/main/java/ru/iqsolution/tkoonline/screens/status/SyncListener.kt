package ru.iqsolution.tkoonline.screens.status

import ru.iqsolution.tkoonline.screens.base.BaseActivity

interface SyncListener {

    val baseActivity: BaseActivity<*>?

    fun updateTime()

    fun updateLocation(available: Boolean)

    fun updateConnection(icon: Int)

    fun updateBattery(status: Int, level: Int)
}