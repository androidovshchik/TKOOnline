package ru.iqsolution.tkonline.boxes

import BootboxAlertButtonMap
import BootboxConfirmOptions

open class BootboxConfirm : BootboxDialog(), BootboxConfirmOptions {
    override var callback: (() -> Any)?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var buttons: BootboxAlertButtonMap?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var message: dynamic = null

    override var swapButtonOrder: Boolean? = null

    override var centerVertical: Boolean? = null
}