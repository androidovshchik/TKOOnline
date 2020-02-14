package ru.iqsolution.tkoonline.screens.map

import android.webkit.JavascriptInterface
import java.lang.ref.WeakReference

@Suppress("unused")
class MapJavaScript(listener: MapListener) {

    private val reference = WeakReference(listener)

    @JavascriptInterface
    fun onReady() {
        reference.get()?.onReady()
    }

    @JavascriptInterface
    fun onPlatform(kpId: Int) {
        reference.get()?.onPlatform(kpId)
    }
}