package org.js.neutralino.core

external class Filesystem {

    fun createDirectory(dirName: Any, s: Any, e: Any)

    fun removeDirectory(dirName: Any, s: Any, e: Any)

    fun writeFile(fileName: Any, content: Any, s: Any, e: Any)

    fun readFile(fileName: Any, s: Any, e: Any)

    fun removeFile(fileName: Any, s: Any, e: Any)

    fun readDirectory(path: Any, s: Any, e: Any)
}