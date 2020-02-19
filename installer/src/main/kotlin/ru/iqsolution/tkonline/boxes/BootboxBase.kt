package ru.iqsolution.tkonline.boxes

import BootboxBaseOptions

abstract class BootboxBase : BootboxBaseOptions {

    override var title: dynamic = null

    override var onEscape: dynamic = null

    override var show: Boolean? = null

    override var backdrop: Boolean? = null

    override var closeButton: Boolean? = null

    override var animate: Boolean? = null

    override var className: String? = null

    override var size = "small"

    override var locale: String? = null

    override var scrollable: Boolean? = null
}