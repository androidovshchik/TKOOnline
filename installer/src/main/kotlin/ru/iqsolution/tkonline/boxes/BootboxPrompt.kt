package ru.iqsolution.tkonline.boxes

import BootboxConfirmPromptButtonMap
import BootboxPromptOptions
import InputOption

open class BootboxPrompt : BootboxBase(), BootboxPromptOptions {
    override var callback: (result: String) -> Any
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var buttons: BootboxConfirmPromptButtonMap?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var value: String? = null

    override var inputType: String? = null

    override var inputOptions: Array<InputOption>? = null
}