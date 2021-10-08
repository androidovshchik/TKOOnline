package ru.iqsolution.tkoonline.extensions

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import ru.iqsolution.tkoonline.remote.api.ServerError
import timber.log.Timber

fun retrofit2.Response<*>.parseErrors(): List<ServerError> {
    return parseErrors(errorBody()?.string() ?: return emptyList())
}

fun okhttp3.Response.parseErrors(): List<ServerError> {
    return parseErrors(peekBody(Long.MAX_VALUE).string())
}

private fun parseErrors(body: String?): List<ServerError> {
    if (body.isNullOrBlank()) {
        return listOf()
    }
    try {
        var response = JSONTokener(body).nextValue()
        if (response is JSONObject) {
            response = response.get("errors")
        }
        return when (response) {
            is JSONArray -> mutableListOf<ServerError>().apply {
                (0 until response.length()).forEach { i ->
                    val error = response.getJSONObject(i)
                    add(ServerError().apply {
                        code = error.getString("code")
                        description = error.getString("description")
                    })
                }
            }
            is JSONObject -> listOf(ServerError().apply {
                code = response.getString("code")
                description = response.getString("description")
            })
            else -> listOf()
        }
    } catch (e: Throwable) {
        Timber.e(e)
    }
    return listOf()
}