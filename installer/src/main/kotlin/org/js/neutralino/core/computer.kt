package org.js.neutralino.core

external class Ram {

    var available: Int

    var total: Int
}

external class RamData {

    var ram: Ram
}

external interface Computer {

    fun getRamUsage(s: Success<RamData>, e: Error)
}