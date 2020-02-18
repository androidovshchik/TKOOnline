package org.js.neutralino.core

external interface Settings {

    fun getSettings(s: (Any) -> Unit, e: (dynamic) -> Unit)
}