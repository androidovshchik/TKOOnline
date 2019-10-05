@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.graphics.Typeface
import android.text.InputFilter
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.DigitsKeyListener
import android.text.style.StyleSpan
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

fun TextView.setTextBoldSpan(text: CharSequence, vararg array: Int) {
    require(array.size % 2 == 0)
    val boldStyle = StyleSpan(Typeface.BOLD)
    setText(SpannableStringBuilder(text).apply {
        for (i in array.indices step 2) {
            setSpan(boldStyle, array[i], array[i + 1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    })
}