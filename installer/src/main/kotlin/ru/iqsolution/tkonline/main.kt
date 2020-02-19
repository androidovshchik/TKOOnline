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

@Suppress("SpellCheckingInspection", "CascadeIf")
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
            OSName.WINDOWS -> "app\\tools\\adb.exe"
            OSName.LINUX -> "app/tools/adb-linux"
            else -> {
                showError(
                    """
                    Данная ОС не поддерживается.
                    Откройте эту программу на ПК с ОС Windows или Linux
                """.trimIndent()
                )
                return@addEventListener
            }
        }
        val packageName = "ru.iqsolution.tkoonline"
        findFile(".", ".apk") { apk ->
            findFile("app/tools", adb.substring("app/tools/".length)) {
                execCommand("$adb kill-server && $adb start-server") {
                    execCommand("$adb install -r -t $apk") { install ->
                        if (install.contains("Success")) {
                            execCommand(
                                """
                                $adb shell pm grant $packageName android.permission.CAMERA &&
                                $adb shell pm grant $packageName android.permission.ACCESS_FINE_LOCATION &&
                                $adb shell dumpsys deviceidle whitelist +$packageName
                            """.trimIndent().replace("\n", " ")
                            ) { grant ->
                                if (grant.contains("Added: $packageName")) {
                                    execCommand("$adb shell dpm set-device-owner $packageName/.receivers.AdminReceiver") { owner ->
                                        if (owner.contains("Success: Device owner")) {
                                            showPrompt(
                                                "Готово", """
                                                Приложение успешно установлено в режиме киоска
                                            """.trimIndent()
                                            )
                                        } else if (owner.isEmpty()) {
                                            showPrompt(
                                                "Внимание", """
                                                Если вы входили в аккаунт Google, то требуется сброс до заводских настроек.
                                                В противном случае, на устройстве уже есть приложение в режиме киоска
                                            """.trimIndent()
                                            )
                                        } else {
                                            showError(owner, true)
                                        }
                                    }
                                } else {
                                    showError(grant, true)
                                }
                            }
                        } else if (install.contains("no devices/emulators found")) {
                            showError(
                                """
                                Не найдено устройство.
                                Проверьте, подключено ли устройство к ПК для передачи файлов, 
                                включен ли режим разработчика и отладка по USB
                            """.trimIndent()
                            )
                        } else if (install.contains("set: device offline")) {
                            showError(
                                """
                                Устройство недоступно.
                                Попробуйте заново подключить устройство через USB
                            """.trimIndent()
                            )
                        } else if (install.contains("set: cannot connect to daemon") || install.contains("set: device still authorizing")) {
                            showError(
                                """
                                Не удалось подключиться к устройству.
                                Попробуйте повторить попытку
                            """.trimIndent()
                            )
                        } else if (install.contains("set: device unauthorized")) {
                            showError(
                                """
                                Требуется разрешение на отладку по USB.
                                Подтвердите это во всплывающем диалоге на устройстве
                            """.trimIndent()
                            )
                        } else {
                            showError(install, true)
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
            success(file.trim())
        } else {
            showPrompt(
                "Внимание", """
                Не найден файл $path/$filename".
                Используйте другую копию этой программы
            """.trimIndent().replace("/.", "/*.")
            )
        }
    }, {
        showError("При поиске файла $path/$filename".replace("/.", "/*."))
    })
}

private fun execCommand(command: String, success: (String) -> Unit) {
    Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: $command", {
        Neutralino.os.runCommand(command, { data ->
            Neutralino.debug.log(LogType.INFO, "${Date().toLocaleString()}: ${data.stdout}", {
                success(data.stdout.trim())
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

private fun showError(message: String, unknown: Boolean = false) {
    if (unknown) {
        showPrompt("Неизвестная ошибка", message)
    } else {
        showPrompt("Ошибка", message)
    }
}

private fun showPrompt(title: String, message: String) {
    alertTitle = title
    alertMessage = message
    window.setTimeout({
        waitDialog?.modal("hide")
    }, 500)
}