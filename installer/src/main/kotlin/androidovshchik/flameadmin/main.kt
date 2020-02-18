package androidovshchik.flameadmin

import org.js.neutralino.Neutralino
import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    val button = document.getElementById("install") as HTMLButtonElement
    button.addEventListener("click", {
        Neutralino.os.runCommand("adb", {
            window.alert(it.stdout)
        }, {
            window.alert(it?.toString() ?: "error")
        })
    })
}