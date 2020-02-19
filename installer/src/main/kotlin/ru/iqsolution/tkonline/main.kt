package ru.iqsolution.tkonline

import bootbox
import org.js.neutralino.Neutralino
import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document

fun main() {
    bootbox.setLocale("ru")
    val button = document.getElementById("install") as HTMLButtonElement
    button.addEventListener("click", {
        //val dialog = bootbox.dialog(BootboxWait());
        /*bootbox.confirm("fddfsdf") {

        } as Unit*/
// do something in the background
        //dialog.modal("hide");
        Neutralino.os.runCommand("app/tools/adb-linux install -r -t app/assets/tkoonline-release.apk", {
            bootbox.confirm(BootboxConfirm("success: ${it.stdout}")) as Unit
        }, {
            bootbox.confirm(BootboxConfirm("error: ${it?.toString()}")) as Unit
        })
    })
}