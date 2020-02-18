package org.js.neutralino.core

external class InitOptions {

    var load: () -> Unit

    var pingSuccessCallback: () -> Unit

    var pingFailCallback: () -> Unit
}