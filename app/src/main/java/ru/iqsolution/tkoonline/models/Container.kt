package ru.iqsolution.tkoonline.models

interface Container {

    var containerType: String

    var containerVolume: Float

    var containerCount: Int

    val isEmpty: Boolean
        get() = containerVolume < 0.1f

    fun addContainer(container: Container) {
        if (isEmpty) {
            containerVolume = container.containerVolume
        }
        containerCount += container.containerCount
    }
}