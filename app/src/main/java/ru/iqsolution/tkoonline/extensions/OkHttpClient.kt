package ru.iqsolution.tkoonline.extensions

import okhttp3.OkHttpClient

fun OkHttpClient.cancelAll(tag: Any?) {
    for (call in dispatcher.queuedCalls()) {
        if (call.request().tag() == tag) {
            call.cancel()
        }
    }
    for (call in dispatcher.runningCalls()) {
        if (call.request().tag() == tag) {
            call.cancel()
        }
    }
}