@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.widget.EditText

// todo add to dialogs
fun EditText.setTextSelection(text: CharSequence?) {
    (text ?: "").let {
        setText(it)
        setSelection(it.length)
    }
}