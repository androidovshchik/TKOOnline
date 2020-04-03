package ru.iqsolution.tkoonline.extensions

import org.apache.commons.text.StringEscapeUtils

@Suppress("NOTHING_TO_INLINE")
inline fun StringBuilder.appendN(value: String?, unescape: Boolean = false) {
    if (unescape) {
        append(StringEscapeUtils.unescapeJava(value))
    } else {
        append(value)
    }.append('\n')
}