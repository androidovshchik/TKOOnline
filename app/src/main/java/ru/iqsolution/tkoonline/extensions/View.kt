@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.view.View

inline fun <reified T> View.makeCallback(action: T.() -> Unit) {
    context.activity()?.let {
        if (it is T && !it.isFinishing) {
            action(it)
        }
    }
}