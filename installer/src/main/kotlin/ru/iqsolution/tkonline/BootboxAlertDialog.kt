package ru.iqsolution.tkonline

import BootboxButton

class BootboxAlertDialog(val title: String, val message: String) {

    val centerVertical = true

    val buttons = arrayOf(
        BootboxButton().apply {
            label = "OK"
            className = "btn-success"
        }
    )
}