package org.js.neutralino.core

external class Filesystem {

    fun createDirectory(dirName: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun removeDirectory(dirName: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun writeFile(fileName: String, content: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun readFile(fileName: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun removeFile(fileName: String, s: (Any) -> Unit, e: (dynamic) -> Unit)

    fun readDirectory(path: String, s: (Any) -> Unit, e: (dynamic) -> Unit)
}