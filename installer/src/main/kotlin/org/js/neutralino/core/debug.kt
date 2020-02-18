package org.js.neutralino.core

external enum class sdfsdf {
    INFO, ERROR, WARN
}

external class asdasd {

    var message: String
}

external interface Debug {

    fun log(type: String, message: String, s: (Any) -> Unit, e: (dynamic) -> Unit)
}