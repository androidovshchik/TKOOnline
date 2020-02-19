package ru.iqsolution.tkonline

import BootboxButton

class BootboxAlertDialog(val title: String, val message: String) {

    val centerVertical = true

    val buttons = arrayOf(
        BootboxButton().apply {
            label = if (title == "Ошибка") "Закрыть" else "OK"
            className = when (title) {
                "Ошибка" -> "btn-danger"
                "Внимание" -> "btn-warning"
                else -> "btn-success"
            }
        }
    )
}