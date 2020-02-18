package org.js.neutralino.core

external class Storage {

    fun putData(data: Any, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun getData(bucket: String, s: (Any) -> Unit, e: (dynamic) -> Unit)
}