package org.js.neutralino.core

external class StorageData {

    var bucket: String

    var content: Any
}

external interface Storage {

    fun putData(data: StorageData, s: (Any) -> Unit, e: Error)

    fun getData(bucket: String, s: (Any) -> Unit, e: Error)
}