package ru.iqsolution.tkonline.boxes

import BootboxDialogOptions

abstract class BootboxDialog : BootboxBase(), BootboxDialogOptions {

    override var swapButtonOrder: Boolean? = null

    override var centerVertical: Boolean? = null
}