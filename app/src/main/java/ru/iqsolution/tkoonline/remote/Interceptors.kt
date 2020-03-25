package ru.iqsolution.tkoonline.remote

import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import retrofit2.Invocation
import ru.iqsolution.tkoonline.exitUnexpected
import ru.iqsolution.tkoonline.extensions.bgToast
import ru.iqsolution.tkoonline.extensions.parseErrors
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.models.ServerError
import java.lang.ref.WeakReference

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tag(val value: String)

class AppInterceptor(context: Context) : Interceptor, KodeinAware {

    override val kodein by closestKodein(context)

    private val reference = WeakReference(context)

    private val preferences: Preferences by instance()

    private var address = preferences.mainServerAddress

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
        val errors = response.parseErrors()
        val firstError = errors.firstOrNull()
        val codes = errors.map { it.code }
        reference.get()?.run {
            when (tag) {
                "login" -> {
                    when (response.code) {
                        401 -> {
                            when {
                                codes.contains("fail to auth") -> bgToast("Неверный логин или пароль")
                                codes.contains("car already taken") -> bgToast("Данная ТС уже авторизована в системе - Обратитесь к Вашему администратору")
                                else -> echoError(response, firstError)
                            }
                        }
                        403 -> bgToast("Доступ запрещен, обратитесь к администратору")
                        404 -> bgToast("Сервер не отвечает, проверьте настройки соединения")
                        else -> echoError(response, firstError)
                    }
                }
                "platforms", "photos" -> {
                    when (response.code) {
                        400, 401 -> {
                            when {
                                codes.contains("closed token") -> {
                                    bgToast("Ваша авторизация сброшена, пожалуйста, авторизуйтесь заново")
                                    exitUnexpected()
                                }
                                else -> echoError(response, firstError)
                            }
                        }
                        403 -> {
                            bgToast("Доступ запрещен, обратитесь к администратору")
                            exitUnexpected()
                        }
                        else -> echoError(response, firstError)
                    }
                }
                "clean", "photo", "logout" -> {
                    when (response.code) {
                        400, 401, 403 -> {
                        }
                        else -> echoError(response, firstError)
                    }
                }
                else -> {
                }
            }
        }
        return response
    }
}

fun Context.echoError(response: Response, error: ServerError?) {
    val unknown = when (response.code) {
        400, 401, 403, 404, 500 -> false
        else -> true
    }
    bgToast(error?.print(unknown) ?: "Ошибка ${response.code} ${response.request}")
}