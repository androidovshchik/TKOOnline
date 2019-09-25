package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded

class PhotoEventTypeToken {

    @Embedded
    lateinit var photo: PhotoEvent

    @Embedded
    lateinit var type: PhotoType

    @Embedded
    lateinit var token: AccessToken
}