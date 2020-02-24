package ru.iqsolution.tkoonline

import org.js.neutralino.NL_OS
import org.js.neutralino.OSName
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document

var waitDialog: dynamic = null

fun install() {
    val updateContent = document.getElementById("install-content") as HTMLDivElement
    updateContent.style.display = "none"
    val installContent = document.getElementById("install-content") as HTMLDivElement
    installContent.style.display = "block"
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
                                Попробуйте повторить попытку установки
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
    })
}