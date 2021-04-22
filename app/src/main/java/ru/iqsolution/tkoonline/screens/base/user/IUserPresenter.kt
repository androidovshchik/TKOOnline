package ru.iqsolution.tkoonline.screens.base.user

import ru.iqsolution.tkoonline.screens.base.IBasePresenter

interface IUserPresenter<V : IUserView> : IBasePresenter<V> {

    fun calculateSend()

    fun loadRoute()
}