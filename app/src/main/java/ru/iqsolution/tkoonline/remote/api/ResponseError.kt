package ru.iqsolution.tkoonline.remote.api

import android.content.Context
import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.extensions.bgToast

class ServerError {

    @SerializedName("code")
    var code: String? = null

    @SerializedName("description")
    var description: String? = null

    fun echo(context: Context, unknown: Boolean = false) = context.run {
        bgToast("Ошибка $code : \"$description\" ${if (unknown) "Обратитесь к администратору" else "попробуйте позже"}")
    }
}

class ResponseError {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("errors")
    var errors = listOf<ServerError>()
}