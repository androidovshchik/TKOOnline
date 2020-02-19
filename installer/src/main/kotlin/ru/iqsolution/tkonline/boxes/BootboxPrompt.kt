package ru.iqsolution.tkonline.boxes

import BootboxConfirmPromptButtonMap
import BootboxPromptOptions
import InputOption

abstract class BootboxPrompt : BootboxBase(), BootboxPromptOptions {

    override var buttons: BootboxConfirmPromptButtonMap? = null

    override var value: String? = null

    override var inputType: String? = null

    override var inputOptions: Array<InputOption>? = null
}