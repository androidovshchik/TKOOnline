package ru.iqsolution.tkoonline.extensions

@Suppress("NOTHING_TO_INLINE")
inline fun StringBuilder.appendN(value: String?) {
    append(value).append('\n')
}