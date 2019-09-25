package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class PlatformContainersPhoto {

    @Embedded
    lateinit var platform: Platform

    @Relation(parentColumn = "p_kp_id", entityColumn = "p_linked_id", entity = Platform::class)
    lateinit var platforms: List<Platform>

    @Relation(parentColumn = "p_kp_id", entityColumn = "pe_kp_id", entity = PhotoEvent::class)
    lateinit var photos: List<PhotoEvent>
}