@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens.base

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import org.jetbrains.anko.inputMethodManager
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import ru.iqsolution.tkoonline.extensions.activityCallback

abstract class BaseDialogFragment : DialogFragment(), DIAware {

    override val di by closestDI()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppDialog(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        return null
    }

    override fun dismiss() {
        context?.inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        super.dismiss()
    }

    inline fun <reified T> activityCallback(action: T.() -> Unit) {
        context?.activityCallback(action)
    }
}