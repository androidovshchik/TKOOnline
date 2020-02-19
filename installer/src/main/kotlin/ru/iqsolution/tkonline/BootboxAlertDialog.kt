package ru.iqsolution.tkonline

import BootboxButton

class BootboxAlertDialog(val title: String, val message: String) {

    val centerVertical = true

    val buttons = arrayOf(
        BootboxButton().apply {
            label = if (title.contains("Ошибка", true)) "Закрыть" else "OK"
            className = when (title) {
                "Неизвестная ошибка", "Ошибка" -> "btn-danger"
                "Внимание" -> "btn-warning"
                else -> "btn-success"
            }
        }
    )
}