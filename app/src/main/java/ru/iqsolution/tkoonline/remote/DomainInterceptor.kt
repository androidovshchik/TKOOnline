package ru.iqsolution.tkoonline.remote

import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import ru.iqsolution.tkoonline.local.Preferences

class DomainInterceptor(context: Context) : Interceptor {

    private val preferences = Preferences(context)

    private var address = preferences.mainServerAddress

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        // NOTICE refreshing address only on login request
        if (url.endsWith("auth")) {
            address = preferences.mainServerAddress
        }
        return chain.proceed(
            request.newBuilder()
                .url(
                    url.replace("localhost", address)
                        .toHttpUrlOrNull() ?: request.url
                )
                .build()
        )
    }
}