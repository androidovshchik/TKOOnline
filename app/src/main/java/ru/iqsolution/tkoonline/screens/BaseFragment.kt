@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

@Suppress("unused")
open class BaseFragment : Fragment(), KodeinAware, IBaseView {

    override val kodein by kodein()

    protected val appContext: Context?
        get() = activity?.applicationContext

    protected val args: Bundle
        get() = arguments ?: Bundle()
}