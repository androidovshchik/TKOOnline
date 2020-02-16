package ru.iqsolution.tkoonline.models

import java.io.Serializable

/**
 * Special class for linked platforms
 */
class SimpleContainer(type: ContainerType) : Serializable, Container {

    override var containerType: String = type.id

    override var containerVolume = 0f

    override var containerCount = 0

    override fun toString(): String {
        return "SimpleContainer(" +
            "containerType='$containerType', " +
            "containerVolume=$containerVolume, " +
            "containerCount=$containerCount" +
            ")"
    }
}