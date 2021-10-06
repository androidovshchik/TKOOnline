@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

val CharSequence?.authHeader: String?
    get() = if (!isNullOrBlank()) "Bearer $this" else null

inline fun <R : CharSequence> R?.ifNullOrBlank(defaultValue: () -> R): R =
    if (this == null || isBlank()) defaultValue() else this