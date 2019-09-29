@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.base

import android.app.Fragment
import android.os.Bundle

@Suppress("MemberVisibilityCanBePrivate")
open class BaseFragment : Fragment() {

    protected val args: Bundle
        get() = arguments ?: Bundle()
}