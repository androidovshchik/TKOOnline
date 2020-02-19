@file:Suppress("unused")

external interface BootboxBaseOptions {

    var title: dynamic /* String | Element */

    var callback: dynamic /* ((result: T) -> Any)? */

    var onEscape: dynamic /* () -> Any | Boolean */

    var show: Boolean?

    var backdrop: Boolean?

    var closeButton: Boolean?

    var animate: Boolean?

    var className: String?

    var size: String /* "small" | "sm" | "large" | "lg" | "extra-large" | "xl" */

    var locale: String?

    var buttons: dynamic /* BootboxButtonMap? */

    var scrollable: Boolean?
}

external interface BootboxDialogOptions : BootboxBaseOptions {

    var message: dynamic /* JQuery | Array<Any> | Element | DocumentFragment | Text | String | (index: Number, html: String) -> dynamic */

    var swapButtonOrder: Boolean?

    var centerVertical: Boolean?
}

external interface BootboxAlertOptions : BootboxDialogOptions {

    override var callback: (() -> Any)?

    override var buttons: BootboxAlertButtonMap?
}

external interface BootboxConfirmOptions : BootboxDialogOptions {

    override var callback: (result: Boolean) -> Any

    override var buttons: BootboxConfirmPromptButtonMap?
}

external interface InputOption {

    var text: String

    var value: String

    var group: String?
}

external interface BootboxPromptOptions : BootboxBaseOptions {

    override var title: String

    var value: String?

    var inputType: String? /* "text" | "textarea" | "email" | "select" | "checkbox" | "date" | "time" | "number" | "password" | "radio" | "range" */

    override var callback: (result: String) -> Any

    override var buttons: BootboxConfirmPromptButtonMap?

    var inputOptions: Array<InputOption>?
}

external interface BootboxDefaultOptions {

    var locale: String?

    var show: Boolean?

    var backdrop: Boolean?

    var closeButton: Boolean?

    var animate: Boolean?

    var className: String?
}

external interface BootboxButton {

    var label: String?

    var className: String?

    var callback: (() -> Any)?
}

external interface BootboxButtonMap

inline operator fun BootboxButtonMap.get(key: String): dynamic /* BootboxButton | Function<*> */ = asDynamic()[key]

inline operator fun BootboxButtonMap.set(key: String, value: BootboxButton) {
    asDynamic()[key] = value
}

inline operator fun BootboxButtonMap.set(key: String, value: Function<*>) {
    asDynamic()[key] = value
}

external interface BootboxAlertButtonMap : BootboxButtonMap {

    var ok: dynamic /* BootboxButton | Function<*> */
}

external interface BootboxConfirmPromptButtonMap : BootboxButtonMap {

    var confirm: dynamic /* BootboxButton | Function<*> */

    var cancel: dynamic /* BootboxButton | Function<*> */
}

@Suppress("PropertyName")
external interface BootboxLocaleValues {

    var OK: String

    var CANCEL: String

    var CONFIRM: String
}

external interface BootboxStatic {

    fun alert(message: String, callback: () -> Unit = definedExternally): dynamic /* JQuery */

    fun alert(options: BootboxAlertOptions): dynamic /* JQuery */

    fun confirm(message: String, callback: (result: Boolean) -> Unit): dynamic /* JQuery */

    fun confirm(options: BootboxConfirmOptions): dynamic /* JQuery */

    fun prompt(message: String, callback: (result: String) -> Unit): dynamic /* JQuery */

    fun prompt(options: BootboxPromptOptions): dynamic /* JQuery */

    fun dialog(message: String, callback: (result: String) -> Unit = definedExternally): dynamic /* JQuery */

    fun dialog(options: BootboxDialogOptions/*<String>*/): dynamic /* JQuery */

    fun setDefaults(options: BootboxDefaultOptions)

    fun hideAll()

    fun addLocale(name: String, values: BootboxLocaleValues)

    fun removeLocale(name: String)

    fun setLocale(name: String)
}

external val bootbox: BootboxStatic