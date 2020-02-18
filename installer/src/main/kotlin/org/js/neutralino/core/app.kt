package org.js.neutralino.core

external class ExitData {

    var message: String
}

external interface App {

    fun exit(s: (ExitData) -> Unit, e: (dynamic) -> Unit)
}