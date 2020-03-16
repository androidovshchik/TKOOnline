package ru.iqsolution.tkoonline.extensions

import com.google.gson.Gson
import ru.iqsolution.tkoonline.remote.api.ResponseError
import timber.log.Timber

val okhttp3.Response.isAccessError: Boolean
    get() = code == 401 || code == 403

fun retrofit2.Response<*>.parseErrors(gson: Gson): List<String?> {
    return parseErrors(gson, errorBody()?.string() ?: return listOf())
}

fun okhttp3.Response.parseErrors(gson: Gson): List<String?> {
    return parseErrors(gson, body?.string() ?: return listOf())
}

private fun parseErrors(gson: Gson, body: String?): List<String?> {
    Timber.d(body)
    try {
        val responseError = gson.fromJson(body, ResponseError::class.java)
        if (responseError.status == "error") {
            return responseError.errors.map { it.code }
        }
    } catch (e: Throwable) {
        Timber.e(e)
    }
    return listOf()
}