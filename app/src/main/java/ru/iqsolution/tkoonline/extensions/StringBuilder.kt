package ru.iqsolution.tkoonline.extensions

import org.apache.commons.text.StringEscapeUtils

@Suppress("NOTHING_TO_INLINE")
inline fun StringBuilder.appendN(value: String?) {
    append(value).append('\n')
}

@Suppress("NOTHING_TO_INLINE")
inline fun StringBuilder.appendUN(value: String?) {
    append(StringEscapeUtils.unescapeJava(value)).append('\n')
}