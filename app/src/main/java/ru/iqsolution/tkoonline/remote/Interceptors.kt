package ru.iqsolution.tkoonline.remote

import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import ru.iqsolution.tkoonline.exitUnexpected
import ru.iqsolution.tkoonline.extensions.bgToast
import ru.iqsolution.tkoonline.local.Preferences
import java.lang.ref.WeakReference

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tag(val value: String)

class AppInterceptor(context: Context) : Interceptor {

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
        val builder = request.newBuilder()
            .url(url.replace("localhost", address).toHttpUrlOrNull() ?: request.url)
        request.tag(Invocation::class.java)?.let {
            it.method().getAnnotation(Tag::class.java)?.let { tag ->
                builder.tag(tag.value)
            }
        }
        val response = chain.proceed(builder.build())
        reference.get()?.apply {
            when (response.code) {
                400 -> bgToast("Сервер не смог обработать запрос, некорректные данные в запросе")
                401, 403 -> exitUnexpected(true)
                404 -> bgToast("Сервер не отвечает, проверьте настройки соединения")
                500 -> bgToast("Сервер не смог обработать запрос, ошибка на стороне сервера")
            }
        }
        return response
    }
}