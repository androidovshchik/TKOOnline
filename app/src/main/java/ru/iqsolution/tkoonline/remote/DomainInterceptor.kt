package ru.iqsolution.tkoonline.remote

import android.app.ActivityManager
import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import org.jetbrains.anko.*
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.LockActivity
import java.lang.ref.WeakReference

class DomainInterceptor(context: Context) : Interceptor {

    private val reference = WeakReference(context)

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
        val response = chain.proceed(
            request.newBuilder()
                .url(url.replace("localhost", address).toHttpUrlOrNull() ?: request.url)
                .build()
        )
        if (response.code in 401..403) {
            reference.get()?.apply {
                startActivity(intentFor<LockActivity>().apply {
                    if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_LOCKED) {
                        clearTask()
                    } else {
                        clearTop()
                    }
                    newTask()
                })
            }
            throw Throwable()
        }
        return response
    }
}