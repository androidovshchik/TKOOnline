package ru.iqsolution.tkoonline.remote

import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import ru.iqsolution.tkoonline.local.Preferences

class DomainInterceptor(context: Context) : Interceptor {

    private val preferences = Preferences(context)

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(
            request.newBuilder()
                .url(
                    request.url.toString()
                        .replace("localhost", preferences.mainServerAddress)
                        .toHttpUrlOrNull() ?: request.url
                )
                .build()
        )
    }
}