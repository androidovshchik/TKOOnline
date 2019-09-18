@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import org.jetbrains.anko.layoutInflater

fun ViewGroup.inflate(@LayoutRes layout: Int): View {
    return context.layoutInflater.inflate(layout, this, false)
}