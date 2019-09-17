@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.widget.TextView

fun TextView.setMaxLength(length: Int) {
    val array = arrayOfNulls<InputFilter>(1)
    array[0] = InputFilter.LengthFilter(length)
    filters = array
}

fun TextView.setOnlyNumbers() {
    inputType = InputType.TYPE_CLASS_NUMBER
    keyListener = DigitsKeyListener.getInstance("0123456789")
}

fun TextView.setOnlyUppercase() {
    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
    filters = arrayOf(InputFilter.AllCaps())
}