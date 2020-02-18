package org.js.neutralino.core

external class Os {

    fun runCommand(cmd: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun getEnvar(v: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun dialogOpen(t: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun dialogSave(t: String, s: (Any) -> Unit, e: (dynamic) -> Unit)
}