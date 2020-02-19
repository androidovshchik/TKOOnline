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
        Neutralino.debug.log(LogType.ERROR, "${Date().toLocaleString()}: $message", {}, {})
    }
    bootbox.setLocale("ru")
    val button = document.getElementById("install") as HTMLButtonElement
    button.addEventListener("click", {
        waitDialog = bootbox.dialog(BootboxWaitDialog())
        waitDialog?.on("hidden.bs.modal") {
            alertTitle?.also { title ->
                alertMessage?.also { message ->
                    bootbox.dialog(
                        BootboxAlertDialog(
                            title.ifEmpty { "Ошибка" },
                            message.ifEmpty { "Пустое сообщение" }
                        )
                    )
                    alertMessage = null
                }
                alertTitle = null
            }
        }
        val adb = when (NL_OS) {
            OSName.WINDOWS -> "app/tools/adb.exe"
            OSName.LINUX -> "app/tools/adb-linux"
            else -> {
                showError("Данная ОС не поддерживается")
                return@addEventListener
            }
        }
        findFile(".", ".apk") { apk ->
            findFile("app/tools", adb.substringAfterLast("/")) {
                execCommand("$adb kill-server && $adb start-server") {
                    execCommand("$adb install -r -t $apk") {
                        when {
                            it.contains("no devices/emulators found") -> showError(
                                """
                            Не найдено устройство.
                            Проверьте, подключено ли устройство к ПК, включен ли режим разработчика и отладка по USB
                        """.trimIndent()
                            )
                            it.contains("Success") -> {

                            }
                            else -> showError(it)
                        }
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
            showError("Не найден файл $path/$filename".replace("/.", "/*."))
        }
    }, {
        showError("При поиске файла $path/$filename".replace("/.", "/*."))
    })
}

private fun execCommand(command: String, success: (String) -> Unit) {
    Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: $command", {
        Neutralino.os.runCommand(command, { data ->
            Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: ${data.stdout}", {
                success(data.stdout)
            }, {
                showError("При сохранении лога вывода команды")
            })
        }, {
            showError("При выполнении команды $command")
        })
    }, {
        showError("При сохранении лога команды")
    })
}

private fun showError(message: String) {
    showPrompt("Ошибка", message)
}

private fun showPrompt(title: String, message: String) {
    alertTitle = title
    alertMessage = message
    window.setTimeout({
        waitDialog?.modal("hide")
    }, 500)
}