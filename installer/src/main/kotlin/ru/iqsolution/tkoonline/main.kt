@file:Suppress("UnsafeCastFromDynamic")

package ru.iqsolution.tkoonline

import bootbox
import org.js.neutralino.NL_OS
import org.js.neutralino.Neutralino
import org.js.neutralino.OSName
import org.js.neutralino.core.FileType
import org.js.neutralino.core.LogType
import kotlin.browser.window
import kotlin.js.Date

var alertTitle: String? = null

var alertMessage: String? = null

fun main() {
    window.onerror = { message, _, _, _, _ ->
        Neutralino.debug.log(LogType.ERROR, "${Date().toLocaleString()}: $message", {}, {})
    }
    bootbox.setLocale("ru")
    when (NL_OS) {
        OSName.WINDOWS, OSName.LINUX -> update()
        else -> {
            showError(
                """
                Данная ОС не поддерживается.
                Используйте эту программу на ПК с ОС Windows или Linux
            """.trimIndent()
            )
        }
    }
}

fun findFile(path: String, filename: String, success: (String) -> Unit) {
    Neutralino.filesystem.readDirectory(path, { data ->
        val file = data?.files?.firstOrNull { it.type == FileType.FILE && it.name.contains(filename) }?.name
        if (file != null) {
            success(file.trim())
        } else {
            showPrompt(
                "Внимание", """
                Не найден файл $path/$filename".
                Попробуйте запустить программу с правами администратора
            """.trimIndent().replace("/.", "/*.")
            )
        }
    }, {
        showError("При поиске файла $path/$filename".replace("/.", "/*."))
    })
}

fun execCommand(command: String, success: (String) -> Unit) {
    Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: $command", {
        Neutralino.os.runCommand(command, { data ->
            Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: ${data?.stdout}", {
                if (data != null) {
                    success(data.stdout.trim())
                } else {
                    showPrompt(
                        "Внимание", """
                        Не удалось выполнить команду.
                        Попробуйте запустить эту программу через BAT файл
                    """.trimIndent().replace("/.", "/*.")
                    )
                }
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

fun showError(message: String, unknown: Boolean = false) {
    if (unknown) {
        showPrompt("Неизвестная ошибка", message)
    } else {
        showPrompt("Ошибка", message)
    }
}

fun showPrompt(title: String, message: String) {
    alertTitle = title
    alertMessage = message
    window.setTimeout({
        waitDialog?.modal("hide")
    }, 500)
}