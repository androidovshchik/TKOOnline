package ru.iqsolution.tkoonline.remote

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import ru.iqsolution.tkoonline.exitUnexpected
import ru.iqsolution.tkoonline.extensions.bgToast
import ru.iqsolution.tkoonline.extensions.isAccessError
import ru.iqsolution.tkoonline.extensions.parseErrors
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.remote.api.ServerError
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

    private val gson = GsonBuilder()
        .setLenient()
        .setExclusionStrategies(SerializedNameStrategy())
        .create()

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val tag = request.tag(Invocation::class.java)?.let {
            it.method().getAnnotation(Tag::class.java)?.let { tag ->
                builder.tag(tag.value)
                tag.value
            }
        }
        // NOTICE refreshing address only on login request
        if (tag == "login") {
            address = preferences.mainServerAddress
        }
        builder.url(request.url.toString().replace("localhost", address).toHttpUrlOrNull()!!)
        val response = chain.proceed(builder.build())
        if (response.isSuccessful) {
            return response
        }
        val errors = response.parseErrors(gson)
        val error = errors.firstOrNull()
        val codes = errors.map { it.code }
        when {
            errors.contains("fail to auth") -> bgToast("Неверный логин или пароль")
            errors.contains("car already taken") -> bgToast("Кто-то другой уже авторизовался на данной TC")
        }
        reference.get()?.run {
            when (tag) {
                "login" -> {
                    when (response.code) {
                        301 -> {
                        }
                        400 -> echoError(error)
                        401 -> {
                            when {
                                codes.contains("fail to auth") -> bgToast("Неверный логин или пароль")
                                codes.contains("car already taken") -> bgToast("Данная ТС уже авторизована в системе - Обратитесь к Вашему администратору")
                                else -> echoError(error)
                            }
                        }
                        403 -> bgToast("Доступ запрещен, обратитесь к администратору")
                        404 -> bgToast("Сервер не отвечает, проверьте настройки соединения")
                        else -> echoError(error, true)
                    }
                    if (response.code == 401) {

                    }
                }
                "platforms", "photos" -> {
                    if (response.isAccessError) {
                        exitUnexpected()
                    }
                }
                "clean" -> {
                }
                "photo" -> {
                }
                "logout" -> {
                }
                "version" -> {
                }
            }
        }
        when (response.code) {
            //400 -> bgToast("Сервер не смог обработать запрос, некорректные данные в запросе")
            401 -> if (url.endsWith("v1/auth")) {
                val errors = response.parseErrors(gson)
                when {
                    errors.contains("fail to auth") -> bgToast("Неверный логин или пароль")
                    errors.contains("car already taken") -> bgToast("Кто-то другой уже авторизовался на данной TC")
                }
            } else {
                exitUnexpected()
            }
            403 -> if (!url.contains("v1/auth")) {
                exitUnexpected()
            }
            404 -> bgToast("Сервер не отвечает, проверьте настройки соединения")
            500 -> bgToast("Сервер не смог обработать запрос, ошибка на стороне сервера")
        }
        return response
    }

    private fun Context.echoError(error: ServerError?, unknown: Boolean = false) {
        if (error != null) {
            bgToast("Ошибка ${error.code}: \"${error.description}\" попробуйте позже${""}")
        }
    }
}