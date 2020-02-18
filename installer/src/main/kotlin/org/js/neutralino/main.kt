package org.js.neutralino

import org.js.neutralino.core.*

external var Neutralino: NeutralinoJs

external class NeutralinoJs {

    var app: App

    var filesystem: Filesystem

    var settings: Settings

    var os: Os

    var computer: Computer

    var storage: Storage

    var debug: Debug
}