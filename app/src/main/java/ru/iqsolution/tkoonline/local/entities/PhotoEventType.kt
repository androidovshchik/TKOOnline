package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded

class PhotoEventType {

    @Embedded
    lateinit var photo: PhotoEvent

    @Embedded
    lateinit var type: PhotoType
}