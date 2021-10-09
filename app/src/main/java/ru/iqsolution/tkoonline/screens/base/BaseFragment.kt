@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.base

import android.app.Fragment
import android.os.Bundle
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import ru.iqsolution.tkoonline.extensions.doActivityCallback

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFragment : Fragment(), DIAware {

    override val di by closestDI()

    @Suppress("unused")
    protected val args: Bundle
        get() = arguments ?: Bundle()

    inline fun <reified T> activityCallback(action: T.() -> Unit) {
        context?.doActivityCallback(action)
    }
}