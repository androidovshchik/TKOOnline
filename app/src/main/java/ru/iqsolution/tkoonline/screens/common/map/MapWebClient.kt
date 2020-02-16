package ru.iqsolution.tkoonline.screens.common.map

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MapWebClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String?) = true

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest?) = true
}