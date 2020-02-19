package ru.iqsolution.tkonline

import bootbox
import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document

fun main() {
    val button = document.getElementById("install") as HTMLButtonElement
    button.addEventListener("click", {
        val dialog = bootbox.dialog(BootboxWait("Please wait while we do something..."));

// do something in the background
        //dialog.modal("hide");
        /*Neutralino.os.runCommand("app/tools/adb-linux install -r -t app/assets/tkoonline-release.apk", {
            window.alert("success: ${it.stdout}")
        }, {
            window.alert("error: ${it?.toString()}")
        })*/
    })
}