package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class PlatformContainersPhotoClean {

    @Embedded
    lateinit var platform: Platform

    @Relation(parentColumn = "p_kp_id", entityColumn = "p_linked_id", entity = Platform::class)
    lateinit var platforms: List<Platform>

    @Relation(parentColumn = "p_kp_id", entityColumn = "pe_kp_id", entity = PhotoEvent::class)
    lateinit var photos: List<PhotoEvent>

    @Relation(parentColumn = "p_kp_id", entityColumn = "ce_kp_id", entity = CleanEvent::class)
    lateinit var cleanup: List<CleanEvent>
}