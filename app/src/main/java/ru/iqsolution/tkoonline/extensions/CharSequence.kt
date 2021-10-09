@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

val CharSequence.authHeader: String
    get() = "Bearer $this"

val CharSequence.asPhone: String
    get() = toString().replace("[^+0-9*#]".toRegex(), "")

inline fun <R : CharSequence> R?.ifNullOrBlank(defaultValue: () -> R): R =
    if (this == null || isBlank()) defaultValue() else this