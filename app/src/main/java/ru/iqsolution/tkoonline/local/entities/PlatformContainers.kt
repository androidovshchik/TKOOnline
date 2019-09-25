package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class PlatformContainers {

    @Embedded
    lateinit var platform: Platform

    @Relation(parentColumn = "ID", entityColumn = "Задача", entity = Platform::class)
    lateinit var platforms: List<Platform>
}