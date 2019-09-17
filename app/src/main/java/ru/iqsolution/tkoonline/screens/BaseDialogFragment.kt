@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens

import android.app.DialogFragment
import kotlinx.coroutines.*
import org.jetbrains.anko.inputMethodManager
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
open class BaseDialogFragment : DialogFragment(), CoroutineScope {

    protected val baseJob = SupervisorJob()

    override fun dismiss() {
        context?.inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        super.dismiss()
    }

    override fun onDestroyView() {
        baseJob.cancelChildren()
        super.onDestroyView()
    }

    override val coroutineContext = Dispatchers.Main + baseJob + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}