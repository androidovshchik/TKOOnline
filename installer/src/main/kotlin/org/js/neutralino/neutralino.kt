package org.js.neutralino

import org.js.neutralino.core.*

external var NL_OS: String

external var NL_NAME: String

external var NL_PORT: String

external var NL_MODE: String

external var NL_VERSION: String

external interface NeutralinoJs {

    var app: App

    var filesystem: Filesystem

    var settings: Settings

    var os: OS

    var computer: Computer

    var storage: Storage

    fun init(options: InitOptions)

    var debug: Debug
}

external var Neutralino: NeutralinoJs