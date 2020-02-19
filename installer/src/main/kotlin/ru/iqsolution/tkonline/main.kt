@file:Suppress("UnsafeCastFromDynamic")

package ru.iqsolution.tkonline

import bootbox
import org.js.neutralino.NL_OS
import org.js.neutralino.Neutralino
import org.js.neutralino.OSName
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
            promptMessage?.also {
                bootbox.alert(BootboxAlert(it))
                promptMessage = null
            }
        }
        val adb = when (NL_OS) {
            OSName.WINDOWS -> "adb.exe"
            OSName.LINUX -> "adb-linux"
            else -> {
                showError("Текущая ОС не поддерживается")
                return@addEventListener
            }
        }
        findFile(".", ".apk") { apk ->
            findFile("tools", adb) { _ ->
                execCommand("") {

                }
            }
        }
        /*Neutralino.os.runCommand("app/tools/adb-linux install -r -t app/assets/tkoonline-release.apk", {
            bootbox.confirm(BootboxConfirm("success: ${it.stdout}"))
        }, {
            bootbox.confirm(BootboxConfirm("error: ${it?.toString()}"))
        })*/
    })
}

private fun findFile(path: String, filename: String, success: (String) -> Unit) {
    Neutralino.filesystem.readDirectory(path, { data ->
        val file = data?.files?.firstOrNull { it.type == FileType.FILE && it.name.contains(filename) }?.name
        if (file != null) {
            success(file)
        } else {
            showError("Не найден файл $path/$filename".replace("/.", "*"))
        }
    }, {
        showPrompt("Ошибка при поиске файла $path/$filename".replace("/.", "*"))
    })
}

private fun execCommand(command: String, success: () -> Unit) {

}

private fun showError(message: String) {
    showPrompt("Ошибка: ${message.decapitalize()}")
}

private fun showPrompt(message: String) {
    promptMessage = message
    window.setTimeout({
        waitDialog?.modal("hide")
    }, 500)
}