package ru.iqsolution.tkoonline.extensions

import org.json.JSONArray
import org.json.JSONObject
import ru.iqsolution.tkoonline.models.ServerError
import timber.log.Timber

fun retrofit2.Response<*>.parseErrors(): List<ServerError> {
    return parseErrors(errorBody()?.string() ?: return listOf())
}

fun okhttp3.Response.parseErrors(): List<ServerError> {
    return parseErrors(body?.string() ?: return listOf())
}

private fun parseErrors(body: String?): List<ServerError> {
    if (body.isNullOrBlank()) {
        return listOf()
    }
    try {
        val response = JSONObject(body)
        if (response.getString("status") == "error") {
            return when (val errors = response.get("errors")) {
                is JSONArray -> {
                    return mutableListOf<ServerError>().apply {
                        (0 until errors.length()).forEach { i ->
                            val error = errors.getJSONObject(i)
                            add(ServerError().apply {
                                code = error.getString("code")
                                description = error.getString("description")
                            })
                        }
                    }
                }
                is JSONObject -> listOf(ServerError().apply {
                    code = errors.getString("code")
                    description = errors.getString("description")
                })
                else -> listOf()
            }
        }
    } catch (e: Throwable) {
        Timber.e(e)
    }
    return listOf()
}