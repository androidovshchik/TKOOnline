package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.text.TextUtils
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
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
class MapView : FrameLayout {

    private var lat: Double? = null

    private var lon: Double? = null

    private var isReady = false

    private val calls = arrayListOf<String>()

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
        runCommand("mapZoomIn1($duration)")
    }

    private fun zoomOut(duration: Int = 500) {
        runCommand("mapZoomOut1($duration)")
    }

    fun moveTo(latitude: Double, longitude: Double, zoom: Int = 12, duration: Int = 500) {
        runCommand("mapMoveTo2($latitude, $longitude, $zoom, $duration)")
    }

    fun clearMarkers() {
        runCommand("mapClearMarkers3()")
    }

    fun setMarkers(first: String, second: String = "[]") {
        runCommand("mapSetMarkers3($first, $second)")
    }

    fun clearLocation() {
        lat = null
        lon = null
        runCommand("mapClearLocation4()")
    }

    /**
     * Should be called regularly from outside
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Int = 0) {
        lat = latitude
        lon = longitude
        runCommand("mapSetLocation4($latitude, $longitude, $radius)")
    }

    fun clearState(all: Boolean = false) {
        runCommand("mapClearState5($all)")
    }

    fun saveState() {
        runCommand("mapSaveState5()")
    }

    @Synchronized
    private fun runCommand(call: String?) {
        if (isReady) {
            val task = TextUtils.join(";", calls)
            calls.clear()
            loadUrl("javascript:")
        } else {
            calls.add(call)
        }
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

    inner class Client : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            isReady = false
        }

        override fun onPageFinished(view: WebView, url: String) {
            Timber.d(url)
            isReady = true
            runCommand(null)
        }
    }

    companion object {

        init {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
    }
}