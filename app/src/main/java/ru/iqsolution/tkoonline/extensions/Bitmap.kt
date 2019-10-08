@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.graphics.Bitmap
import timber.log.Timber

inline fun <T> Bitmap.use(block: Bitmap.() -> T): T {
    try {
        return block()
    } finally {
        try {
            recycle()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }
}