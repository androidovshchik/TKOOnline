@file:Suppress("UnsafeCastFromDynamic")

package ru.iqsolution.tkonline

import bootbox
import org.js.neutralino.Neutralino
import org.js.neutralino.core.FileType
import org.js.neutralino.core.LogType
import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document
import kotlin.browser.window

var waitDialog: dynamic = null

var promptMessage: String? = null

fun main() {
    window.onerror = { message, _, _, _, _ ->
        Neutralino.debug.log(LogType.ERROR, message, {}, {})
    }
    bootbox.setLocale("ru")
    val button = document.getElementById("install") as HTMLButtonElement
    button.addEventListener("click", {
        waitDialog = bootbox.dialog(BootboxWaitDialog())
        waitDialog?.on("hidden.bs.modal") {
            promptMessage?.let {
                bootbox.alert(BootboxAlert(it))
            }
        }
        Neutralino.filesystem.readDirectory(".", { data ->
            val apkFilename = data?.files?.firstOrNull { it.type == FileType.FILE && it.name.endsWith(".apk") }?.name
            if (apkFilename == null) {
                showPrompt("Не найден apk файл")
                return@readDirectory
            }
            showPrompt("success: $apkFilename")
        }, {
            showPrompt()
        })
        /*Neutralino.os.runCommand("app/tools/adb-linux install -r -t app/assets/tkoonline-release.apk", {
            bootbox.confirm(BootboxConfirm("success: ${it.stdout}"))
        }, {
            bootbox.confirm(BootboxConfirm("error: ${it?.toString()}"))
        })*/
    })
}

private fun showPrompt(message: String = "Неизвестная ошибка") {
    promptMessage = message
    window.setTimeout({
        waitDialog?.modal("hide")
    }, 500)
}