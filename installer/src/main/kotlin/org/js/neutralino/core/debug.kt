package org.js.neutralino.core

external class Debug {

    fun log(type: String, message: String, s: (Any) -> Unit, e: (dynamic) -> Unit)
}