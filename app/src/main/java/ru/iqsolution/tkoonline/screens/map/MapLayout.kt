package ru.iqsolution.tkoonline.screens.map

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.merge_map.view.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.models.SimpleLocation
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
class MapLayout : FrameLayout {

    private var mLatitude: Double? = null

    private var mLongitude: Double? = null

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
        View.inflate(context, R.layout.merge_map, this)
        map.webViewClient = Client()
        map_plus.setOnClickListener {
            zoomIn()
        }
        map_minus.setOnClickListener {
            zoomOut()
        }
        map_location.setOnClickListener {
            mLatitude?.let { latitude ->
                mLongitude?.let { longitude ->
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
        mLatitude = null
        mLongitude = null
        runCall("_4_mapClearLocation()")
    }

    fun setLocation(location: SimpleLocation?, radius: Int = 0) {
        location?.let {
            setLocation(it.latitude, it.longitude, radius)
        }
    }

    /**
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Int = 0) {
        mLatitude = latitude
        mLongitude = longitude
        runCall("_4_mapSetLocation($latitude, $longitude, $radius)")
    }

    fun clearState(all: Boolean = false) {
        runCall("_5_mapClearState($all)")
    }

    fun saveState() {
        runCall("_5_mapSaveState()")
    }

    private fun runCall(call: String) {
        // NOTICE currently supports only 1 digit, don't use split
        if (isReady) {
            loadUrl("javascript:$call")
        } else {
            calls.apply {
                removeAll { it[1] == call[1] }
                add(call)
            }
        }
    }

    fun release() {
        map.destroy()
    }

    override fun hasOverlappingRendering() = false

    inner class Client : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            Timber.d("onPageStarted: $url")
            map_tools.visibility = GONE
            isReady = false
            calls.clear()
        }

        override fun onPageFinished(view: WebView, url: String) {
            Timber.d("onPageFinished: $url")
            map_tools.visibility = VISIBLE
            isReady = true
            val js = TextUtils.join(";", calls)
            calls.clear()
            if (!TextUtils.isEmpty(js)) {
                loadUrl("javascript:$js")
            }
        }
    }
}