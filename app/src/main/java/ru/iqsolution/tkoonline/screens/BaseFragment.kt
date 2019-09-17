@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens

import android.app.Fragment
import android.os.Bundle
import kotlinx.coroutines.*
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
open class BaseFragment : Fragment(), CoroutineScope {

    protected val baseJob = SupervisorJob()

    protected val args: Bundle
        get() = arguments ?: Bundle()

    override fun onDestroyView() {
        baseJob.cancelChildren()
        super.onDestroyView()
    }

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}