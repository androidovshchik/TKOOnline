package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView

class MapView : WebView {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        settings.apply {
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            domStorageEnabled = true
        }
    }

    fun zoomIn(duration: Int = 500) {
        loadUrl("javascript:mapZoomIn($duration)")
    }

    fun zoomOut(duration: Int = 500) {
        loadUrl("javascript:mapZoomOut($duration)")
    }

    fun moveTo(latitude: Double, longitude: Double, zoom: Int = 12, duration: Int = 500) {
        loadUrl("javascript:mapMoveTo($latitude, $longitude, $zoom, $duration)")
    }

    fun clearMarkers() {
        loadUrl("javascript:mapClearMarkers()")
    }

    fun setMarkers(first: String, second: String = "[]") {
        loadUrl("javascript:mapSetMarkers($first, $second)")
    }

    fun clearLocation() {
        loadUrl("javascript:mapClearLocation()")
    }

    /**
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Int = 0) {
        loadUrl("javascript:mapSetLocation($latitude, $longitude, $radius)")
    }

    fun clearState() {
        loadUrl("javascript:mapClearState()")
    }

    fun saveState() {
        loadUrl("javascript:mapSaveState()")
    }

    override fun hasOverlappingRendering() = false
}