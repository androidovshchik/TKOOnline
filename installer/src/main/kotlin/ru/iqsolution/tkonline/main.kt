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
import kotlin.js.Date

var waitDialog: dynamic = null

var alertTitle: String? = null

var alertMessage: String? = null

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
                bootbox.dialog(BootboxAlertDialog("title", it))
                promptMessage = null
            }
        }
        val adb = when (NL_OS) {
            OSName.WINDOWS -> "adb.exe"
            OSName.LINUX -> "adb-linux"
            else -> {
                showError("Данная ОС не поддерживается")
                return@addEventListener
            }
        }
        findFile(".", ".apk") { apk ->
            findFile("app/tools", adb) {
                execCommand("app/tools/$adb install -r -t $apk") {
                    when {
                        it.contains("no devices/emulators found") -> showError("Не найдено устройство")
                        it.contains("Success") -> {

                        }
                        else -> showPrompt("it.stdout")
                    }
                }
            }
        }
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

private fun execCommand(command: String, success: (String) -> Unit) {
    Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: $command", {
        Neutralino.os.runCommand(command, { data ->
            Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: ${data.stdout}", {
                success(data.stdout)
            }, {
                showPrompt("Ошибка при сохранении лога команды")
            })
        }, {
            showPrompt("Ошибка при выполнении команды $command")
        })
    }, {
        showPrompt("Ошибка при сохранении лога команды")
    })
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