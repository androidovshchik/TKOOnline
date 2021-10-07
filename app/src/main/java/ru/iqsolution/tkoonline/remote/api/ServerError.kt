package ru.iqsolution.tkoonline.remote.api

/**
 * "errors": {
 *     "code": "closed token",
 *	   "description": "token already closed",
 *	   "meta": {
 *		   "valid_from": "2020-03-19T10:19:23+03:00",
 *		   "valid_till": "2020-03-19T10:19:46+03:00"
 *	   }
 * }
 */
class ServerError {

    var code: String? = null

    var description: String? = null

    fun print(unknown: Boolean = false): String {
        return "Ошибка $code : \"$description\" ${if (unknown) "Обратитесь к администратору" else "попробуйте позже"}"
    }
}