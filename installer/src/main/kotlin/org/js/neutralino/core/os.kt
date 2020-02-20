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

    fun runCommand(command: String, s: Success<CommandData?>, e: Error)

    fun getEnvar(key: String, s: Success<EnvarData>, e: Error)

    fun dialogOpen(title: String, s: Success<DialogData>, e: Error)

    fun dialogSave(title: String, s: Success<DialogData>, e: Error)
}