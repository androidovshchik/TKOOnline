package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.map_view.view.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R

@Suppress("MemberVisibilityCanBePrivate")
class MapView : FrameLayout {

    var latitude: Double? = null

    var longitude: Double? = null

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
        View.inflate(context, R.layout.map_view, this)
        map.settings.apply {
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setAppCacheEnabled(true)
            setAppCachePath(context.cacheDir.path)
        }
        map_plus.onClick {
            zoomIn()
        }
        map_minus.onClick {
            zoomOut()
        }
        map_location.onClick {
            latitude?.let { lat ->
                longitude?.let { lon ->
                    moveTo(lat, lon)
                }
            }
        }
    }

    private fun zoomIn(duration: Int = 500) {
        map.loadUrl("javascript:mapZoomIn($duration)")
    }

    private fun zoomOut(duration: Int = 500) {
        map.loadUrl("javascript:mapZoomOut($duration)")
    }

    private fun moveTo(latitude: Double, longitude: Double, zoom: Int = 12, duration: Int = 500) {
        map.loadUrl("javascript:mapMoveTo($latitude, $longitude, $zoom, $duration)")
    }

    fun clearMarkers() {
        map.loadUrl("javascript:mapClearMarkers()")
    }

    fun setMarkers(first: String, second: String = "[]") {
        map.loadUrl("javascript:mapSetMarkers($first, $second)")
    }

    fun clearLocation() {
        latitude = null
        longitude = null
        map.loadUrl("javascript:mapClearLocation()")
    }

    /**
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Int = 0) {
        map.loadUrl("javascript:mapSetLocation($latitude, $longitude, $radius)")
    }

    fun clearState() {
        map.loadUrl("javascript:mapClearState()")
    }

    fun saveState() {
        map.loadUrl("javascript:mapSaveState()")
    }

    override fun hasOverlappingRendering() = false

    companion object {

        init {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
    }
}