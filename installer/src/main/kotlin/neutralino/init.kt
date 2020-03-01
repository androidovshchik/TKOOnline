package neutralino

external class InitOptions {

    var load: () -> Unit

    var pingSuccessCallback: () -> Unit

    var pingFailCallback: () -> Unit
}