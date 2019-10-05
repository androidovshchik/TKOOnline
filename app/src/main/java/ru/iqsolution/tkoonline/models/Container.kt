package ru.iqsolution.tkoonline.models

interface Container {

    var containerType: String

    var containerVolume: Float

    var containerCount: Int

    val isEmpty: Boolean
        get() = containerVolume < 0.1f

    fun reset() {
        containerVolume = 0f
        containerCount = 0
    }

    fun setFrom(container: Container?) {
        container?.let {
            if (toContainerType() == it.toContainerType()) {
                containerVolume = it.containerVolume
                containerCount = it.containerCount
            }
        }
    }

    fun addContainer(container: Container?) {
        container?.let {
            if (toContainerType() == it.toContainerType()) {
                if (isEmpty) {
                    containerVolume = it.containerVolume
                }
                containerCount += it.containerCount
            }
        }
    }

    fun toContainerType() = ContainerType.fromId(containerType)
}