@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

inline fun <R : CharSequence> R?.ifNullOrBlank(defaultValue: () -> R): R =
    if (this == null || isBlank()) defaultValue() else this