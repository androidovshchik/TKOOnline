@file:Suppress("DEPRECATION")

import kotlin.js.Promise

external interface AxiosResponse<T> {

    var data: T

    var status: Number

    var statusText: String

    var headers: Any
}

external class AxiosPromise<T> : Promise<AxiosResponse<T>>

external interface AxiosInstance {

    fun <T> get(url: String, config: Any = definedExternally): AxiosPromise<T>
}

external var Axios: AxiosInstance