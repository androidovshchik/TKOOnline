@file:Suppress("UnsafeCastFromDynamic")

package ru.iqsolution.tkonline

import bootbox
import org.js.neutralino.Neutralino
import org.js.neutralino.core.FileType
import org.js.neutralino.core.LogType
import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.onerror = { message, _, _, _, _ ->
        Neutralino.debug.log(LogType.ERROR, message, {}, {})
    }
    bootbox.setLocale("ru")
    val button = document.getElementById("install") as HTMLButtonElement
    button.addEventListener("click", {
        val dialog = bootbox.dialog(BootboxWaitDialog());
        /*bootbox.confirm("fddfsdf") {

        } as Unit*/
// do something in the background
        //dialog.modal("hide");
        Neutralino.filesystem.readDirectory(".", {
            if (it != null) {
                it.files.forEach { file ->
                    if (file.type == FileType.FILE) {
                        if (file.name.endsWith(".apk")) {
                            bootbox.alert(BootboxAlert("success: ${file.name}"))
                        }
                    }
                }
            } else {
                bootbox.alert(BootboxAlert("error: ${it.toString()}"))
            }
        }, {
            bootbox.alert(BootboxAlert("error: ${it?.toString()}"))
        })
        /*Neutralino.os.runCommand("app/tools/adb-linux install -r -t app/assets/tkoonline-release.apk", {
            bootbox.confirm(BootboxConfirm("success: ${it.stdout}"))
        }, {
            bootbox.confirm(BootboxConfirm("error: ${it?.toString()}"))
        })*/
    })
}