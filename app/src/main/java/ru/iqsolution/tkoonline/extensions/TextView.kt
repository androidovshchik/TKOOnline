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

fun TextView.setTextBoldSpan(text: CharSequence, indices: List<Int>) {
    require(indices.size % 2 == 0) { "The size of list must be an even number" }
    val boldStyle = StyleSpan(Typeface.BOLD)
    setText(SpannableStringBuilder(text).apply {
        indices.chunked(2).forEach {
            setSpan(boldStyle, it[0], it[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    })
}