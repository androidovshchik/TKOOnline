@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "EXTERNAL_DELEGATION"
)

external interface BootboxBaseOptions<T> {
    var title: dynamic /* String | Element */
        get() = definedExternally
        set(value) = definedExternally
    var callback: ((result: T) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var onEscape: dynamic /* () -> Any | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var show: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var backdrop: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var closeButton: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var animate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
    var size: String /* "small" | "sm" | "large" | "lg" | "extra-large" | "xl" */
    var locale: String?
        get() = definedExternally
        set(value) = definedExternally
    var buttons: BootboxButtonMap?
        get() = definedExternally
        set(value) = definedExternally
    var scrollable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxDialogOptions<T> : BootboxBaseOptions<T> {
    var message: dynamic /* JQuery | Array<Any> | Element | DocumentFragment | Text | String | (index: Number, html: String) -> dynamic */
        get() = definedExternally
        set(value) = definedExternally
    var swapButtonOrder: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var centerVertical: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxAlertOptions : BootboxDialogOptions<Unit> {
    var callback: (() -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    override var buttons: BootboxAlertButtonMap?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxConfirmOptions : BootboxDialogOptions<Boolean> {
    var callback: (result: Boolean) -> Any
    override var buttons: BootboxConfirmPromptButtonMap?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var text: String
    var value: String
    var group: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxPromptOptions : BootboxBaseOptions<String> {
    override var title: String
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
    var inputType: String /* "text" | "textarea" | "email" | "select" | "checkbox" | "date" | "time" | "number" | "password" | "radio" | "range" */
    var callback: (result: String) -> Any
    override var buttons: BootboxConfirmPromptButtonMap?
        get() = definedExternally
        set(value) = definedExternally
    var inputOptions: Array<`T$0`>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxDefaultOptions {
    var locale: String?
        get() = definedExternally
        set(value) = definedExternally
    var show: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var backdrop: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var closeButton: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var animate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxButton {
    var label: String?
        get() = definedExternally
        set(value) = definedExternally
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
    var callback: (() -> Any)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxButtonMap {
    @nativeGetter
    operator fun get(key: String): dynamic /* BootboxButton | Function<*> */

    @nativeSetter
    operator fun set(key: String, value: BootboxButton)

    @nativeSetter
    operator fun set(key: String, value: Function<*>)
}

external interface BootboxAlertButtonMap : BootboxButtonMap {
    var ok: dynamic /* BootboxButton | Function<*> */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxConfirmPromptButtonMap : BootboxButtonMap {
    var confirm: dynamic /* BootboxButton | Function<*> */
        get() = definedExternally
        set(value) = definedExternally
    var cancel: dynamic /* BootboxButton | Function<*> */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BootboxLocaleValues {
    var OK: String
    var CANCEL: String
    var CONFIRM: String
}

external interface BootboxStatic {
    fun alert(message: String, callback: () -> Unit = definedExternally): Any
    fun alert(options: BootboxAlertOptions): Any
    fun confirm(message: String, callback: (result: Boolean) -> Unit): Any
    fun confirm(options: BootboxConfirmOptions): Any
    fun prompt(message: String, callback: (result: String) -> Unit): Any
    fun prompt(options: BootboxPromptOptions): Any
    fun dialog(message: String, callback: (result: String) -> Unit = definedExternally): Any
    fun dialog(options: BootboxDialogOptions<String>): Any
    fun setDefaults(options: BootboxDefaultOptions)
    fun hideAll()
    fun addLocale(name: String, values: BootboxLocaleValues)
    fun removeLocale(name: String)
    fun setLocale(name: String)
}

@JsModule("bootbox")
external val bootbox: BootboxStatic