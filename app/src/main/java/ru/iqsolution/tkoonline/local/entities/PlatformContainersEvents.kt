package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class PlatformContainersEvents {

    @Embedded
    lateinit var platform: Platform

    @Relation(parentColumn = "pf_kp_id", entityColumn = "pf_linked_id", entity = Platform::class)
    lateinit var platforms: List<Platform>

    @Relation(parentColumn = "pf_kp_id", entityColumn = "pe_kp_id", entity = PhotoEvent::class)
    lateinit var photoEvents: List<PhotoEvent>
}