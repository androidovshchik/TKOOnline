@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.widget.EditText

fun EditText.setTextSelection(text: CharSequence?) {
    text?.let {
        setText(it)
        setSelection(it.length)
    }
}