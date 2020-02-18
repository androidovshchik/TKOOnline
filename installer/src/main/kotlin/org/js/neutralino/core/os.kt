package org.js.neutralino.core

external class CommandData {

    var stdout: String
}

external class EnvarData {

    var value: String
}

external class DialogData {

    var file: String
}

external interface OS {

    fun runCommand(cmd: String, s: (CommandData) -> Unit, e: (dynamic) -> Unit)

    fun getEnvar(v: String, s: (EnvarData) -> Unit, e: (dynamic) -> Unit)

    fun dialogOpen(t: String, s: (DialogData) -> Unit, e: (dynamic) -> Unit)

    fun dialogSave(t: String, s: (DialogData) -> Unit, e: (dynamic) -> Unit)
}