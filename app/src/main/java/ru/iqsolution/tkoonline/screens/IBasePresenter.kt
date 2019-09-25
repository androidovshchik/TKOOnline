package ru.iqsolution.tkoonline.screens

import java.io.File

interface IBasePresenter<V : IBaseView> {

    val isAttached: Boolean

    fun attachView(view: V)

    fun createPhoto(): File

    fun movePhoto(path: String)

    fun deletePhoto(path: String)

    fun clearAuthorization()

    fun detachView()
}