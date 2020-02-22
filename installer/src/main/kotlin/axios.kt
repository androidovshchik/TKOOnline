import kotlin.js.Promise

external interface AxiosTransformer {
    @nativeInvoke
    operator fun invoke(data: Any, headers: Any = definedExternally): Any
}

external interface AxiosAdapter {
    @nativeInvoke
    operator fun invoke(config: AxiosRequestConfig): AxiosPromise<Any>
}

external interface AxiosBasicCredentials {
    var username: String
    var password: String
}

external interface `T$0` {
    var username: String
    var password: String
}

external interface AxiosProxyConfig {
    var host: String
    var port: Number
    var auth: `T$0`?
        get() = definedExternally
        set(value) = definedExternally
    var protocol: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AxiosRequestConfig {

    var url: String?

    var method: String /* 'get' | 'GET' | 'delete' | 'DELETE' | 'head' | 'HEAD' | 'options' | 'OPTIONS' | 'post' | 'POST' | 'put' | 'PUT' | 'patch' | 'PATCH' | 'link' | 'LINK' | 'unlink' | 'UNLINK' */

    var baseURL: String?

    var transformRequest: dynamic /* AxiosTransformer | Array<AxiosTransformer> */

    var transformResponse: dynamic /* AxiosTransformer | Array<AxiosTransformer> */

    var headers: Any?

    var params: Any?

    var paramsSerializer: ((params: Any) -> String)?

    var data: Any?

    var timeout: Number?

    var timeoutErrorMessage: String?

    var withCredentials: Boolean?

    var adapter: AxiosAdapter?

    var auth: AxiosBasicCredentials?

    var responseType: String /* 'arraybuffer' | 'blob' | 'document' | 'json' | 'text' | 'stream' */

    var xsrfCookieName: String?

    var xsrfHeaderName: String?

    var onUploadProgress: ((progressEvent: Any) -> Unit)?

    var onDownloadProgress: ((progressEvent: Any) -> Unit)?

    var maxContentLength: Number?

    var validateStatus: ((status: Number) -> Boolean)?

    var maxRedirects: Number?

    var socketPath: String?

    var httpAgent: Any?

    var httpsAgent: Any?

    var proxy: dynamic /* AxiosProxyConfig | Boolean */

    var cancelToken: CancelToken?
}

external interface AxiosResponse<T> {

    var data: T

    var status: Number

    var statusText: String

    var headers: Any

    var config: AxiosRequestConfig

    var request: Any?
}

external interface AxiosPromise<T> : Promise<AxiosResponse<T>>

external interface CancelStatic

external interface Cancel {
    var message: String
}

external interface Canceler {
    @nativeInvoke
    operator fun invoke(message: String = definedExternally)
}

external interface CancelTokenStatic {
    fun source(): CancelTokenSource
}

external interface CancelToken {

    var promise: Promise<Cancel>

    var reason: Cancel?

    fun throwIfRequested()
}

external interface CancelTokenSource {

    var token: CancelToken

    var cancel: Canceler
}

external interface AxiosInterceptorManager<V> {

    fun use(onFulfilled: (value: V) -> dynamic = definedExternally, onRejected: (error: Any) -> Any = definedExternally): Number

    fun eject(id: Number)
}

external interface `T$1` {

    var request: AxiosInterceptorManager<AxiosRequestConfig>

    var response: AxiosInterceptorManager<AxiosResponse<Any>>
}

external interface AxiosInstance {
    @nativeInvoke
    operator fun invoke(config: AxiosRequestConfig): AxiosPromise<Any>
    @nativeInvoke
    operator fun invoke(url: String, config: AxiosRequestConfig = definedExternally): AxiosPromise<Any>
    var defaults: AxiosRequestConfig
    var interceptors: `T$1`
    fun getUri(config: AxiosRequestConfig = definedExternally): String
    fun <T, R> request(config: AxiosRequestConfig): Promise<R>
    fun <T, R> get(url: String, config: AxiosRequestConfig = definedExternally): Promise<R>
    fun <T, R> delete(url: String, config: AxiosRequestConfig = definedExternally): Promise<R>
    fun <T, R> head(url: String, config: AxiosRequestConfig = definedExternally): Promise<R>
    fun <T, R> options(url: String, config: AxiosRequestConfig = definedExternally): Promise<R>
    fun <T, R> post(url: String, data: Any = definedExternally, config: AxiosRequestConfig = definedExternally): Promise<R>
    fun <T, R> put(url: String, data: Any = definedExternally, config: AxiosRequestConfig = definedExternally): Promise<R>
    fun <T, R> patch(url: String, data: Any = definedExternally, config: AxiosRequestConfig = definedExternally): Promise<R>
}

external interface AxiosStatic : AxiosInstance {
    fun create(config: AxiosRequestConfig = definedExternally): AxiosInstance
    var Cancel: CancelStatic
    var CancelToken: CancelTokenStatic
    fun isCancel(value: Any): Boolean
    fun <T> all(values: Array<dynamic /* T | Promise<T> */>): Promise<Array<T>>
    fun <T, R> spread(callback: (args: Array<T>) -> R): (array: Array<T>) -> R
}

external var Axios: AxiosStatic