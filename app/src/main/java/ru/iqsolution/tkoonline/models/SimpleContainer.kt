package ru.iqsolution.tkoonline.models

/**
 * Special class for linked platforms to summarize them
 */
class SimpleContainer : Container {

    override lateinit var containerType: String

    override var containerVolume = 0f

    override var containerCount = 0
}