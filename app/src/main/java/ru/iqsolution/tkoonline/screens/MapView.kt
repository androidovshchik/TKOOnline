package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.map_view.view.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.extensions.activity
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
class MapView : FrameLayout {

    private var lat: Double? = null

    private var lon: Double? = null

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
        map.apply {
            webViewClient = Client()
            settings.apply {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setAppCacheEnabled(true)
                setAppCachePath(context.cacheDir.path)
            }
        }
        map_plus.onClick {
            zoomIn()
        }
        map_minus.onClick {
            zoomOut()
        }
        map_location.onClick {
            lat?.let { latitude ->
                lon?.let { longitude ->
                    moveTo(latitude, longitude)
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
        lat = null
        lon = null
        loadUrl("javascript:mapClearLocation()")
    }

    /**
     * Should be called regularly from outside
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Int = 0) {
        lat = latitude
        lon = longitude
        loadUrl("javascript:mapSetLocation($latitude, $longitude, $radius)")
    }

    fun clearState(all: Boolean = false) {
        loadUrl("javascript:mapClearState($all)")
    }

    fun saveState() {
        loadUrl("javascript:mapSaveState()")
    }

    fun release() {
        try {
            (parent as ViewGroup?)?.removeView(this)
        } catch (e: Exception) {
        }
        try {
            removeAllViews()
        } catch (e: Exception) {
        }
        map.destroy()
    }

    override fun hasOverlappingRendering() = false

    interface Listener {

        fun onPageFinished(url: String)
    }

    inner class Client : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            Timber.d(url)
            context.activity()?.let {
                if (it is Listener && !it.isFinishing) {
                    it.onPageFinished(url)
                }
            }
        }
    }

    companion object {

        init {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
    }
}