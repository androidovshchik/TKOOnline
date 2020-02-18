package org.js.neutralino.core

external enum class LogType {
    INFO, ERROR, WARN
}

external class LogData {

    var message: String
}

external interface Debug {

    fun log(type: LogType, message: String, s: Success<LogData>, e: Error)
}