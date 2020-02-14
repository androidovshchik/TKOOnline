package ru.iqsolution.tkoonline.screens.map

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.WorkerThread
import kotlinx.android.synthetic.main.merge_map.view.*
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.models.SimpleLocation

@Suppress("MemberVisibilityCanBePrivate")
class MapLayout : FrameLayout, MapListener {

    private var mLatitude: Double? = null

    private var mLongitude: Double? = null

    private var isReady = false

    private var hasInteracted = false

    private val calls = arrayListOf<String>()

    private val readyRunnable = Runnable {
        isReady = true
        map_tools.visibility = VISIBLE
        val js = TextUtils.join(";", calls)
        calls.clear()
        if (!TextUtils.isEmpty(js)) {
            map_web.loadUrl("javascript:$js")
        }
    }

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
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        View.inflate(context, R.layout.merge_map, this)
        map_web.addJavascriptInterface(MapJavaScript(this), "Android")
        map_plus.setOnClickListener {
            hasInteracted = true
            zoomIn()
        }
        map_minus.setOnClickListener {
            hasInteracted = true
            zoomOut()
        }
        map_location.setOnClickListener {
            mLatitude?.let { latitude ->
                mLongitude?.let { longitude ->
                    hasInteracted = false
                    moveTo(latitude, longitude)
                }
            }
        }
    }

    @WorkerThread
    override fun onReady() {
        map_web.post(readyRunnable)
    }

    @WorkerThread
    override fun onPlatform(kpId: Int) {

    }

    /**
     * IMPORTANT should be called with urls only
     */
    fun loadUrl(url: String) {
        isReady = false
        map_tools.visibility = GONE
        calls.clear()
        map_web.loadUrl(url)
    }

    fun setBounds(lat1: Int, lon1: Int, lat2: Int, lon2: Int) {
        runCall("_1_mapSetBounds($lat1, $lon1, $lat2, $lon2)")
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

    fun setLocation(location: SimpleLocation?) {
        location?.let {
            setLocation(it.latitude, it.longitude, it.accuracy)
        }
    }

    /**
     * @param radius in meters
     */
    fun setLocation(latitude: Double, longitude: Double, radius: Float = 0f) {
        mLatitude = latitude
        mLongitude = longitude
        runCall("_4_mapSetLocation($latitude, $longitude, $radius, $isActive)")
    }

    fun clearState(all: Boolean = false) {
        runCall("_5_mapClearState($all)")
    }

    fun saveState() {
        runCall("_5_mapSaveState()")
    }

    fun changeIcon(active: Boolean) {
        isActive = active
        runCall("_6_mapChangeIcon($active)")
    }

    fun clearRoute() {
        runCall("_7_mapClearRoute()")
    }

    fun setRoute(locations: String) {
        runCall("_7_mapSetRoute($locations)")
    }

    private fun runCall(call: String) {
        if (isReady) {
            map_web.loadUrl("javascript:$call")
        } else {
            // NOTICE currently supports only 1 digit, don't use split because of large text is some cases
            calls.apply {
                removeAll { it[1] == call[1] }
                add(call)
            }
        }
    }

    fun release() {
        try {
            removeAllViews()
        } catch (e: Throwable) {
        }
        map_web.destroy()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                hasInteracted = true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun hasOverlappingRendering() = false

    companion object {

        // location
        private var isActive = false
    }
}