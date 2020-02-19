@file:Suppress("unused")

class BootboxButton {

    var label: String? = null

    var className: String? = null

    var callback: (() -> Any)? = null
}

external interface BootboxStatic {

    fun alert(message: String, callback: (() -> Unit)?): dynamic /* JQuery */

    fun alert(options: Any): dynamic /* JQuery */

    fun confirm(message: String, callback: (result: Boolean) -> Unit): dynamic /* JQuery */

    fun confirm(options: Any): dynamic /* JQuery */

    fun prompt(message: String, callback: (result: String) -> Unit): dynamic /* JQuery */

    fun prompt(options: Any): dynamic /* JQuery */

    fun dialog(message: String, callback: ((result: String) -> Unit)?): dynamic /* JQuery */

    fun dialog(options: Any): dynamic /* JQuery */

    fun setDefaults(options: Any)

    fun hideAll()

    fun addLocale(name: String, values: Any)

    fun removeLocale(name: String)

    fun setLocale(name: String)
}

external val bootbox: BootboxStatic