package ru.iqsolution.tkoonline.models

interface Container {

    var containerType: String

    var containerVolume: Float

    var containerCount: Int

    fun reset() {
        containerVolume = 0f
        containerCount = 0
    }

    fun setFromEqual(container: Container?): Boolean {
        container?.let {
            if (containerType == it.containerType) {
                containerVolume = it.containerVolume
                containerCount = it.containerCount
                return true
            }
        }
        return false
    }

    fun setFromAny(container: Container?): Boolean {
        container?.let {
            containerType = it.containerType
            containerVolume = it.containerVolume
            containerCount = it.containerCount
            return true
        }
        return false
    }

    fun toContainerType() = ContainerType.fromId(containerType)
}