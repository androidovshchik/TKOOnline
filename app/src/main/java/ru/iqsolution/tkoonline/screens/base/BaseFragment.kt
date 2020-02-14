@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.base

import android.app.Fragment
import android.os.Bundle
import ru.iqsolution.tkoonline.extensions.makeCallback

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFragment : Fragment() {

    protected val args: Bundle
        get() = arguments ?: Bundle()

    inline fun <reified T> makeCallback(action: T.() -> Unit) {
        context?.makeCallback(action)
    }
}