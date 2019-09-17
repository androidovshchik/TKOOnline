@file:Suppress("DEPRECATION")

package ru.iqsolution.tkoonline.screens

import android.app.DialogFragment
import org.jetbrains.anko.inputMethodManager

@Suppress("MemberVisibilityCanBePrivate")
open class BaseDialogFragment : DialogFragment() {

    override fun dismiss() {
        context?.inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        super.dismiss()
    }
}