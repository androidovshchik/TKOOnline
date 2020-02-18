package ru.iqsolution.tkonline

import org.js.neutralino.Neutralino
import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    val button = document.getElementById("install") as HTMLButtonElement
    button.addEventListener("click", {
        Neutralino.os.runCommand("app/tools/adb-linux install -r -t app/assets/tkoonline-release.apk", {
            window.alert("success: ${it.stdout}")
        }, {
            window.alert("error: ${it?.toString()}")
        })
    })
}