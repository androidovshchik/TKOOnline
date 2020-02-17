package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class CleanEventRelated {

    @Embedded
    lateinit var clean: CleanEvent

    @Relation(parentColumn = "ce_id", entityColumn = "ce_related_id", entity = CleanEvent::class)
    lateinit var events: List<CleanEvent>
}