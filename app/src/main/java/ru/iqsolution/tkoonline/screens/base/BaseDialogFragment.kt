@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.base

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import org.jetbrains.anko.inputMethodManager
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import ru.iqsolution.tkoonline.extensions.makeCallback

abstract class BaseDialogFragment : DialogFragment(), KodeinAware {

    override val kodein by closestKodein()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        return null
    }

    override fun dismiss() {
        context?.inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        super.dismiss()
    }

    inline fun <reified T> makeCallback(action: T.() -> Unit) {
        context?.makeCallback(action)
    }
}