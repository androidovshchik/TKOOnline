package ru.iqsolution.tkoonline.screens.base

import ru.iqsolution.tkoonline.local.FileManager

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    val fileManager: FileManager

    fun attachView(view: V)

    fun clearAuthorization()

    fun detachView()
}