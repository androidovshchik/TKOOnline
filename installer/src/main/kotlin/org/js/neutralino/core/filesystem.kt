package org.js.neutralino.core

@Suppress("EnumEntryName")
external enum class FileType {
    directory, file
}

external class FileData {

    var name: String

    var type: FileType
}

external class DirectoryData {

    var files: Array<FileData>
}

external class FilesystemData {

    var stdout: String
}

external interface Filesystem {

    fun createDirectory(dirName: String, s: Success<FilesystemData>, e: Error)

    fun removeDirectory(dirName: String, s: Success<FilesystemData>, e: Error)

    fun readDirectory(path: String, s: Success<DirectoryData>, e: Error)

    fun writeFile(fileName: String, content: String, s: Success<FilesystemData>, e: Error)

    fun readFile(fileName: String, s: Success<FilesystemData>, e: Error)

    fun removeFile(fileName: String, s: Success<FilesystemData>, e: Error)
}