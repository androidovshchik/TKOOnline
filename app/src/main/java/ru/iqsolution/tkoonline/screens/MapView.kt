package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.map_view.view.*
import org.jetbrains.anko.sdk23.listeners.onClick
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.models.SimpleLocation
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

    fun zoomIn(duration: Int = 500) {
        runCall("_1_mapZoomIn($duration)")
    }

    fun zoomOut(duration: Int = 500) {
        runCall("_1_mapZoomOut($duration)")
    }

    fun moveTo(location: SimpleLocation?, zoom: Int = 12, duration: Int = 500) {
        location?.let {
            moveTo(it.latitude, it.longitude, zoom, duration)
        }
    }

    fun moveTo(latitude: Double, longitude: Double, zoom: Int = 12, duration: Int = 500) {
        runCall("_2_mapMoveTo($latitude, $longitude, $zoom, $duration)")
    }

    fun clearMarkers() {
        runCall("_3_mapClearMarkers()")
    }

    fun setMarkers(first: String, second: String = "[]") {
        // NOTICE here string will be converted to array and objects in js
        runCall("_3_mapSetMarkers($first, $second)")
    }

    fun clearLocation() {
        lat = null
        lon = null
        runCall("_4_mapClearLocation()")
    }

    fun setLocation(location: SimpleLocation?, radius: Int = 0) {
        location?.let {
            setLocation(it.latitude, it.longitude, radius)
        }
    }

    /**
     * Should be called regularly from outside
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Int = 0) {
        lat = latitude
        lon = longitude
        runCall("_4_mapSetLocation($latitude, $longitude, $radius)")
    }

    fun clearState(all: Boolean = false) {
        runCall("_5_mapClearState($all)")
    }

    fun saveState() {
        runCall("_5_mapSaveState()")
    }

    private fun runCall(call: String?) {
        if (call != null) {
            // NOTICE currently supports only 0-9 range, don't use split
            calls.apply {
                if (isNotEmpty()) {
                    removeAll { it.startsWith("_${call[1]}_") }
                }
                add(call)
            }
        }
        if (isReady) {
            if (calls.isNotEmpty()) {
                val js = TextUtils.join(";", calls)
                calls.clear()
                loadUrl("javascript:$js")
            }
        } else {
            if (call == null) {
                calls.clear()
            }
        }
    }

    fun release() {
        map.destroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }

    override fun hasOverlappingRendering() = false

    inner class Client : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            Timber.d("onPageStarted: $url")
            isReady = false
            map_tools.visibility = GONE
            runCall(null)
        }

        override fun onPageFinished(view: WebView, url: String) {
            Timber.d("onPageFinished: $url")
            isReady = true
            map_tools.visibility = VISIBLE
            runCall(null)
        }
    }

    companion object {

        init {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
    }
}