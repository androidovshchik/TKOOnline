package ru.iqsolution.tkoonline.screens.base

interface AdapterListener<T> {

    fun onAdapterEvent(position: Int, item: T, param: Any? = null)
}