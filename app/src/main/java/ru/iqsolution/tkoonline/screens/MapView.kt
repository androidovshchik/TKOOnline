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

    private var latitude: Double? = null

    private var longitude: Double? = null

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

    fun loadUrl(url: String) {
        map.loadUrl(url)
    }

    private fun zoomIn(duration: Int = 500) {
        loadUrl("javascript:mapZoomIn($duration)")
    }

    private fun zoomOut(duration: Int = 500) {
        loadUrl("javascript:mapZoomOut($duration)")
    }

    /**
     * Only current view can move to user location
     */
    private fun moveTo(latitude: Double, longitude: Double, zoom: Int = 12, duration: Int = 500) {
        loadUrl("javascript:mapMoveTo($latitude, $longitude, $zoom, $duration)")
    }

    fun clearMarkers() {
        loadUrl("javascript:mapClearMarkers()")
    }

    fun setMarkers(first: String, second: String = "[]") {
        loadUrl("javascript:mapSetMarkers($first, $second)")
    }

    fun clearLocation() {
        latitude = null
        longitude = null
        loadUrl("javascript:mapClearLocation()")
    }

    /**
     * Should be called regularly from outside
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Int = 0) {
        this.latitude = latitude
        this.longitude = longitude
        loadUrl("javascript:mapSetLocation($latitude, $longitude, $radius)")
    }

    fun clearState() {
        loadUrl("javascript:mapClearState()")
    }

    fun saveState() {
        loadUrl("javascript:mapSaveState()")
    }

    override fun hasOverlappingRendering() = false

    companion object {

        init {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
    }
}