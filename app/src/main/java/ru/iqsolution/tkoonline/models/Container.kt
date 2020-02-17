package ru.iqsolution.tkoonline.models

interface Container {

    var kpId: Int

    var containerType: String

    var containerVolume: Float

    var containerCount: Int

    val isEmpty: Boolean
        get() = containerVolume < 0.09f && containerCount <= 0

    val isInvalid: Boolean
        get() = containerVolume > 0.09f && containerCount <= 0

    fun reset() {
        containerVolume = 0f
        containerCount = 0
    }

    fun setFromEqual(container: Container?): Boolean {
        if (container != null) {
            if (containerType == container.containerType) {
                containerVolume = container.containerVolume
                containerCount = container.containerCount
                return true
            }
        }
        return false
    }

    fun setFromAny(container: Container?): Boolean {
        if (container != null) {
            containerType = container.containerType
            containerVolume = container.containerVolume
            containerCount = container.containerCount
            return true
        }
        return false
    }

    fun toContainerType() = ContainerType.fromId(containerType)
}