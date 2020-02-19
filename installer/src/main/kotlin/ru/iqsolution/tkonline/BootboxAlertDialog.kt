package ru.iqsolution.tkonline

import BootboxButton

class BootboxAlertDialog(val title: String, val message: String) {

    val centerVertical = true

    val buttons = arrayOf(
        BootboxButton().apply {
            label = if (title == "Ошибка") "ЗАКРЫТЬ" else "OK"
            className = if (title == "Ошибка") "btn-danger" else "btn-success"
        }
    )
}