package ru.iqsolution.tkoonline.screens.map

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import ru.iqsolution.tkoonline.BuildConfig

class MapWebView : WebView {

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
        isNestedScrollingEnabled = true
        settings.apply {
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setAppCacheEnabled(true)
            setAppCachePath(context.cacheDir.path)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }

    override fun hasOverlappingRendering() = false

    companion object {

        init {
            setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        }
    }
}