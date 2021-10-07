package ru.iqsolution.tkoonline.local.entities

import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.Location
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.models.Unique
import java.time.OffsetTime

interface BaseTask : Unique, Container, Location<Double> {

    var id: Int?

    var typeId: Int

    var address: String

    var timeLimitFrom: OffsetTime

    var timeLimitTo: OffsetTime

    var status: Int

    fun toPlatformStatus() = PlatformStatus.fromId(status)
}