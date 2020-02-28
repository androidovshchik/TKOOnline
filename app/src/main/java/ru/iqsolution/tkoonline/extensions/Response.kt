package ru.iqsolution.tkoonline.extensions

import com.google.gson.Gson
import retrofit2.Response
import ru.iqsolution.tkoonline.remote.api.ResponseError
import timber.log.Timber

fun Response<*>.parseErrors(gson: Gson): List<String> {
    try {
        val body = errorBody()?.string()
        if (body != null) {
            val error = gson.fromJson(body, ResponseError::class.java)
            if (error.status == "error") {
                return error.errors.map { it.code }
            }
        }
    } catch (e: Throwable) {
        Timber.e(e)
    }
    return listOf()
}