package org.js.neutralino

import org.js.neutralino.core.*

external interface NeutralinoJs {

    var app: App

    var filesystem: Filesystem

    var settings: Settings

    var os: OS

    var computer: Computer

    var storage: Storage

    fun init(options: Any)

    var debug: Debug
}

external var Neutralino: NeutralinoJs