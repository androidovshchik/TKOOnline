package ru.iqsolution.tkoonline.models

interface Container {

    var containerType: String

    var containerVolume: Float

    var containerCount: Int

    fun reset() {
        containerVolume = 0f
        containerCount = 0
    }

    fun setFromEqual(container: Container?) {
        container?.let {
            if (containerType == it.containerType) {
                containerVolume = it.containerVolume
                containerCount = it.containerCount
            }
        }
    }

    fun setFromAny(container: Container?) {
        container?.let {
            containerType = it.containerType
            containerVolume = it.containerVolume
            containerCount = it.containerCount
        }
    }

    fun toContainerType() = ContainerType.fromId(containerType)
}