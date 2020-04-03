package ru.iqsolution.tkoonline.extensions

import org.apache.commons.text.StringEscapeUtils

@Suppress("NOTHING_TO_INLINE")
inline fun StringBuilder.appendN(value: String?) {
    if (value != null) {
        append(StringEscapeUtils.unescapeJava(value)).append('\n')
    }
}