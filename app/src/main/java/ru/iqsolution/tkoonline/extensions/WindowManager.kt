package ru.iqsolution.tkoonline.extensions

import android.graphics.Point
import android.view.WindowManager

val WindowManager.windowSize: Point
    get() {
        val size = Point()
        defaultDisplay.getSize(size)
        return size
    }

val WindowManager.screenSize: Point
    get() {
        val size = Point()
        defaultDisplay.getRealSize(size)
        return size
    }