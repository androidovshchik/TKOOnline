package org.js.neutralino.core

@Suppress("EnumEntryName")
external enum class FileType {
    directory, file
}

external class FileData {

    var name: String

    var type: FileType
}

external class DirData {

    var files: List<FileData>
}

external class FsData {

    var stdout: String
}

external interface Filesystem {

    fun createDirectory(dirName: String, s: Success<FsData>, e: Error)

    fun removeDirectory(dirName: String, s: Success<FsData>, e: Error)

    fun readDirectory(path: String, s: Success<DirData>, e: Error)

    fun writeFile(fileName: String, content: String, s: Success<FsData>, e: Error)

    fun readFile(fileName: String, s: Success<FsData>, e: Error)

    fun removeFile(fileName: String, s: Success<FsData>, e: Error)
}