@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens

import android.app.Fragment
import android.content.Context
import android.os.Bundle

@Suppress("unused")
open class BaseFragment : Fragment(), IBaseView {

    protected val appContext: Context?
        get() = activity?.applicationContext

    protected val args: Bundle
        get() = arguments ?: Bundle()

    override fun showLoading() {}

    override fun hideLoading() {}
}